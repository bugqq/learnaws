package aa.gao.aws.s3.basic;

import java.io.File;
import java.io.IOException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class PutObject {

//    	private static String bucketName     = "*** Provide bucket name ***";
//    	private static String keyName        = "*** Provide key name ***";
//    	private static String uploadFileName = "*** Provide file name with full path ***";
    	private static String bucketName     = "cdc-dev-portal-5";
    	private static String keyName        = "cio_winzip_18.5-64bit.exe-2-4";
    	private static String uploadFileName = "c:\\cio_winzip_18.5-64bit.exe";  //58,854KB
    	 	
    	public static void main(String[] args) throws IOException {
            AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider()); 
            s3Client.setRegion(Region.getRegion(Regions.US_WEST_1));
            
            // Use Amazon S3 Transfer Acceleration endpoint.           
//            s3Client.setS3ClientOptions(S3ClientOptions.builder().setAccelerateModeEnabled(true).build());
            
            try {
            	System.out.println("Uploading a new object to S3 from a file\n");
                File file = new File(uploadFileName);
                System.out.println("buckentname = " + bucketName + " keyName= " + keyName + " uploadFileName=" + uploadFileName);
                long startTime = System.currentTimeMillis();
                s3Client.putObject(new PutObjectRequest(
                		                 bucketName, keyName, file));
                long endTime = System.currentTimeMillis();
                System.out.println("Spending time is " + (endTime - startTime)*1.0/1000 + " seconds");
                System.out.println("finished! buckentname = " + bucketName + " keyName= " + keyName + " uploadFileName=" + uploadFileName);

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
}
/**
运行结果：
Uploading a new object to S3 from a file

buckentname = cdc-dev-portal-5 keyName= cio_winzip_18.5-64bit.exe-2 uploadFileName=c:\cio_winzip_18.5-64bit.exe
Spending time is 183.932 seconds
finished! buckentname = cdc-dev-portal-5 keyName= cio_winzip_18.5-64bit.exe-2 uploadFileName=c:\cio_winzip_18.5-64bit.exe

执行时间 183秒，对比 AccelerateUploadSingleObject，时间确实长
*
*/