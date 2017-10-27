package aa.gao.aws.s3.multi;

import java.io.File;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;

public class UploadObjectMultipartUploadUsingHighLevelAPI {

    public static void main(String[] args) throws Exception {
//        String existingBucketName = "cdc-dev-portal-6";
//        String keyName            = "test/test-upload-mjp-mul-7";
//        String filePath           = "c:\\\\cio_winzip_18.5-64bit.exe";
        
        String existingBucketName     = "cdc-dev-portal-2";
    	String keyName        = "test1/tes2t_multi_aaalog-WAIT";
    	String filePath = "C:\\tmp\\tomcat8\\mdm\\online.log";
        
        TransferManager tm = new TransferManager(new ProfileCredentialsProvider());

        System.out.println("Hello");
        // TransferManager processes all transfers asynchronously, 
        // so this call will return immediately.
        Upload upload = tm.upload(
        		existingBucketName, keyName, new File(filePath));
        System.out.println("Hello2");

        try {
        	// Or you can block and wait for the upload to finish
        	upload.waitForCompletion();
        	System.out.println("Upload complete.");
        } catch (AmazonClientException amazonClientException) {
        	System.out.println("Unable to upload file, upload was aborted.");
        	amazonClientException.printStackTrace();
        }
    }
}
/**
运行结果：
Hello
Hello2
Upload complete.

不需要设置region，自动使用了bucket的region

如果有这句话，upload.waitForCompletion();（不被comment掉的场合）程序就不会自动终了
*/