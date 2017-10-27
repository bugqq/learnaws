package aa.gao.aws.s3.object;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Random;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteVersionRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.SetBucketVersioningConfigurationRequest;

public class DeleteAnObjectVersionEnabledBucket  {

    static String bucketName = "cdc-dev-portal-3";
    static String keyName    = "test/test1.txt"; 
    static AmazonS3Client s3Client;
    
    public static void main(String[] args) throws IOException {
        s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
        try {
            // Make the bucket version-enabled.
            enableVersioningOnBucket(s3Client, bucketName);
            
            // Add a sample object.
            String versionId = putAnObject(keyName);

            s3Client.deleteVersion(
                    new DeleteVersionRequest(
                            bucketName, 
                            keyName,
                            versionId));
            
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException.");
            System.out.println("Error Message: " + ace.getMessage());
        }

    }
    
    static void enableVersioningOnBucket(AmazonS3Client s3Client,
            String bucketName) {
        BucketVersioningConfiguration config = new BucketVersioningConfiguration()
                .withStatus(BucketVersioningConfiguration.ENABLED);
        SetBucketVersioningConfigurationRequest setBucketVersioningConfigurationRequest = new SetBucketVersioningConfigurationRequest(
                bucketName, config);
        s3Client.setBucketVersioningConfiguration(setBucketVersioningConfigurationRequest);
    }
    
    static String putAnObject(String keyName) {
        String content = "This is the content body!";
        String key = "ObjectToDelete-" + new Random().nextInt();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setHeader("Subject", "Content-As-Object");
        metadata.setHeader("Content-Length", (long)content.length());
        PutObjectRequest request = new PutObjectRequest(bucketName, key,
                new ByteArrayInputStream(content.getBytes()), metadata)
                .withCannedAcl(CannedAccessControlList.AuthenticatedRead);
        PutObjectResult response = s3Client.putObject(request);
        return response.getVersionId();
    }
}
/**

 要注意：
 1）如果加上（long）的话，就会出现一个不能包java.lang.Integer转换成java.util.Long的错误
 2）好像不能对其他人创建的bucket进行操作，也许是Permission的问题
 * 
 */
/**
执行结果：
可以从三个图看到，一个是表示有了version管理，一个是show version看到的被标记成为delete的对象，一个是non show version时候什么也看不到。
*/

/**
用deletebucket的程序删除的话，有如下输出
Deleting S3 bucket: cdc-dev-portal-3
 - removing objects from bucket
 - removing objects from bucket bucket_name=cdc-dev-portal-3 key=ObjectToDelete--1023613294
 - removing versions from bucket
 - removing objects from version bucket_name=cdc-dev-portal-3 key=ObjectToDelete--1023613294 versionId=IoIIRgAydPTW1E6QopZyRrHUraOvsfBs
 - removing objects from version bucket_name=cdc-dev-portal-3 key=ObjectToDelete--1023613294 versionId=Yp053XSD3.ooRb8.Ktdnt7nlmzQPvKS8
 OK, bucket ready to delete!
Done!

*/