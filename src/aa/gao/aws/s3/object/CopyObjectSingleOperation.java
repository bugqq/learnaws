package aa.gao.aws.s3.object;

import java.io.IOException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectRequest;

public class CopyObjectSingleOperation {
	private static String bucketName = "cdc-dev-portal-2"; 
	
//	private static String key            = "test/lowerlevel/testlog.log";
//	private static String destinationKey = "test/lowerlevel-copy/testlog-copy.log";
	private static String key            = "test/presignedurl/testlog.log";
	private static String destinationKey = "test/presignedurl-copy/testlog-copy.log";

    public static void main(String[] args) throws IOException {
        AmazonS3 s3client = new AmazonS3Client(new ProfileCredentialsProvider());
        
        try {
            // Copying object
            CopyObjectRequest copyObjRequest = new CopyObjectRequest(
            		bucketName, key, bucketName, destinationKey);
            System.out.println("Copying object.");
            s3client.copyObject(copyObjRequest);

        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, " +
            		"which means your request made it " + 
            		"to Amazon S3, but was rejected with an error " +
                    "response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, " +
            		"which means the client encountered " +
                    "an internal error while trying to " +
                    " communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }
}
/**
执行结果：
Copying object.

注意点，之前使用 test/lowerlevel/testlog.log 进行复制，这个文件19M大小，就出现了TIMEOUT的问题
而换了文件以后复制，29Byte大小，就没有TIMEOUT问题

*/
