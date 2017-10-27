package aa.gao.aws.s3.object;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.SSECustomerKey;

public class ServerSideEncryptionUsingClientSideEncryptionKey {
    private static String bucketName     = "cdc-dev-portal-2";
    private static String keyName        = "MJP/CHINA/PORTAL/BACKUP/mdmlog.log";
    private static String uploadFileName = "C:\\tmp\\tomcat8\\mdm\\error.log";
    private static String targetKeyName  = "MJP/CHINA/PORTAL/MATERIAL/CATALOG DATA/mdmlog.log";
    private static AmazonS3 s3client;

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        s3client = new AmazonS3Client(new ProfileCredentialsProvider());
        try {
            System.out.println("Uploading a new object to S3 from a file\n");
            File file = new File(uploadFileName);
            // Create encryption key.
            SecretKey secretKey = generateSecretKey();
            SSECustomerKey sseKey = new SSECustomerKey(secretKey);

            // 1. Upload object.
            uploadObject(file, sseKey);

            // 2. Download object.
            downloadObject(sseKey);

            // 3. Get object metadata (and verify AES256 encryption).
            retrieveObjectMetadata(sseKey);

            // 4. Copy object (both source and object use SSE-C).
            copyObject(sseKey);

         } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
                    "means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
                    "means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }

    private static void copyObject(SSECustomerKey sseKey) {
        // Create new encryption key for target so it is saved using sse-c
        SecretKey secretKey2 = generateSecretKey();
        SSECustomerKey newSseKey = new SSECustomerKey(secretKey2);

        CopyObjectRequest copyRequest = new CopyObjectRequest(bucketName, keyName, bucketName, targetKeyName)
                .withSourceSSECustomerKey(sseKey)
                .withDestinationSSECustomerKey(newSseKey);

        s3client.copyObject(copyRequest);
        System.out.println("Object copied");
    }

    private static void retrieveObjectMetadata(SSECustomerKey sseKey) {
        GetObjectMetadataRequest getMetadataRequest = new GetObjectMetadataRequest(bucketName, keyName)
                .withSSECustomerKey(sseKey);

        ObjectMetadata objectMetadata =  s3client.getObjectMetadata(getMetadataRequest);
        System.out.println("object size " + objectMetadata.getContentLength());
        System.out.println("Metadata retrieved");
    }

    private static PutObjectRequest uploadObject(File file, SSECustomerKey sseKey) {
        // 1. Upload Object.
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, keyName, file)
                .withSSECustomerKey(sseKey);

        s3client.putObject(putObjectRequest);
        System.out.println("Object uploaded");
        return putObjectRequest;
    }

    private static void downloadObject(SSECustomerKey sseKey) throws IOException {
        // Get a range of bytes from an object.
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, keyName)
                .withSSECustomerKey(sseKey);

        S3Object s3Object = s3client.getObject(getObjectRequest);

        System.out.println("Printing bytes retrieved.");
        displayTextInputStream(s3Object.getObjectContent());
    }

    private static void displayTextInputStream(S3ObjectInputStream input)
    throws IOException {
        // Read one text line at a time and display.
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        while (true) {
            String line = reader.readLine();
            if (line == null) break;

            System.out.println("    " + line);
        }
        System.out.println();
    }

    private static SecretKey generateSecretKey() {
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256, new SecureRandom());
            return generator.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
    }
}
