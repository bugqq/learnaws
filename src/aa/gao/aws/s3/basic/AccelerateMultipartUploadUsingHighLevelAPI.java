package aa.gao.aws.s3.basic;
import java.io.File;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;

import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;

public class AccelerateMultipartUploadUsingHighLevelAPI {
 
//    private static String EXISTING_BUCKET_NAME = "*** Provide bucket name ***";
//    private static String KEY_NAME  = "*** Provide key name ***";
//    private static String FILE_PATH = "*** Provide file name with full path ***";
    
	private static String EXISTING_BUCKET_NAME     = "cdc-dev-portal-6";
	private static String KEY_NAME        = "cio_winzip_18.5-64bit.exe-mul-7";
	private static String FILE_PATH = "c:\\cio_winzip_18.5-64bit.exe";  //58,854KB

	
    public static void main(String[] args) throws Exception {
        
        AmazonS3Client s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
        s3Client.configureRegion(Regions.US_WEST_1);
           
        // Use Amazon S3 Transfer Acceleration endpoint.           
        s3Client.setS3ClientOptions(S3ClientOptions.builder().setAccelerateModeEnabled(true).build());
       
    	TransferManager tm = new TransferManager(s3Client);        
        System.out.println("TransferManager");
        // TransferManager processes all transfers asynchronously, 
        // so this call will return immediately.
        
        System.out.println("buckentname = " + EXISTING_BUCKET_NAME + " keyName= " + KEY_NAME + " uploadFileName=" + FILE_PATH);
        long startTime = System.currentTimeMillis();
        Upload upload = tm.upload(
        		EXISTING_BUCKET_NAME, KEY_NAME, new File(FILE_PATH));
        System.out.println("Upload");
        long endTime = System.currentTimeMillis();
        System.out.println("Spending time is " + (endTime - startTime)*1.0/1000 + " seconds");

        try {
        	// Or you can block and wait for the upload to finish
        	startTime = System.currentTimeMillis();
//        	upload.waitForCompletion();
        	endTime = System.currentTimeMillis();
            System.out.println("Spending time 2 is " + (endTime - startTime)*1.0/1000 + " seconds");
        	System.out.println("Upload complete");
        } catch (AmazonClientException amazonClientException) {
        	System.out.println("Unable to upload file, upload was aborted.");
        	amazonClientException.printStackTrace();
        }
    }
}

/**

执行结果：
TransferManager
buckentname = cdc-dev-portal-6 keyName= cio_winzip_18.5-64bit.exe-mul uploadFileName=c:\cio_winzip_18.5-64bit.exe
Upload
Spending time is 0.015 seconds
Spending time 2 is 12.163 seconds
Upload complete

确实快了 才12秒，对比 前一个SingleUpload的java示例

*/

/**

不会自动结束，挺奇怪的。
试了一下， 去掉upload.waitForCompletion();就可以自动结束了

*/