package aa.gao.aws.s3.multi;

import java.io.File;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;

public class TrackMPUProgressUsingHighLevelAPI {

    public static void main(String[] args) throws Exception {
        String existingBucketName = "cdc-dev-portal-2";
        String keyName            = "testlog-new-lister";
        String filePath           = "C:\\tmp\\tomcat8\\mdm\\online.log";  
        
        TransferManager tm = new TransferManager(new ProfileCredentialsProvider());        

        // For more advanced uploads, you can create a request object 
        // and supply additional request parameters (ex: progress listeners,
        // canned ACLs, etc.)
        PutObjectRequest request = new PutObjectRequest(
        		existingBucketName, keyName, new File(filePath));
        
        // You can ask the upload for its progress, or you can 
        // add a ProgressListener to your request to receive notifications 
        // when bytes are transferred.
        request.setGeneralProgressListener(new ProgressListener() {
			@Override
			public void progressChanged(ProgressEvent progressEvent) {
				System.out.println("Transferred bytes: " + 
						progressEvent.getBytesTransferred());
			}
		});

        // TransferManager processes all transfers asynchronously, 
        // so this call will return immediately.
        Upload upload = tm.upload(request);
        
        try {
        	// You can block and wait for the upload to finish
        	upload.waitForCompletion();
        } catch (AmazonClientException amazonClientException) {
        	System.out.println("Unable to upload file, upload aborted.");
        	amazonClientException.printStackTrace();
        }
    }
}

/*
运行结果：
Transferred bytes: 0
Transferred bytes: 0
Transferred bytes: 0
Transferred bytes: 0
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 8192
Transferred bytes: 0
Transferred bytes: 0
Transferred bytes: 0
Transferred bytes: 0
Transferred bytes: 2602
Transferred bytes: 0
不会自动终了
*/