package aa.gao.aws.s3.object;

import java.io.IOException;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.RestoreObjectRequest;
    
public class RestoreArchivedObject {

    public static String bucketName = "cdc-dev-portal-2"; 
    public static String objectKey =  "test/lowerlevel/testlog.log";
    public static AmazonS3Client s3Client;

    public static void main(String[] args) throws IOException {
        AmazonS3Client s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
        
        try {

          RestoreObjectRequest requestRestore = new RestoreObjectRequest(bucketName, objectKey, 2);
          s3Client.restoreObject(requestRestore);
          
          GetObjectMetadataRequest requestCheck = new GetObjectMetadataRequest(bucketName, objectKey);          
          ObjectMetadata response = s3Client.getObjectMetadata(requestCheck);
          
          Boolean restoreFlag = response.getOngoingRestore();
          System.out.format("Restoration status: %s.\n", 
                  (restoreFlag == true) ? "in progress" : "finished");
            
        } catch (AmazonS3Exception amazonS3Exception) {
            System.out.format("An Amazon S3 error occurred. Exception: %s", amazonS3Exception.toString());
        } catch (Exception ex) {
            System.out.format("Exception: %s", ex.toString());
        }        
    }
}

/*
执行结果：
An Amazon S3 error occurred. Exception: com.amazonaws.services.s3.model.AmazonS3Exception: Restore is not allowed, as object's storage class is not GLACIER (Service: Amazon S3; Status Code: 403; Error Code: InvalidObjectState; Request ID: 2ED716C4699CAD53; S3 Extended Request ID: /ClnPFHWvFSQtA35xPocHVj/mzb6/WLmKNeMODnODpkd9tHIjuI80aJpCklmyZCHnYIrwEm2fsg=), S3 Extended Request ID: /ClnPFHWvFSQtA35xPocHVj/mzb6/WLmKNeMODnODpkd9tHIjuI80aJpCklmyZCHnYIrwEm2fsg=

还不知道如何生成一个GLACIER类型的Object TODO吧
*/