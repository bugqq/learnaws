package aa.gao.aws.s3.multi;

import java.util.Date;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.transfer.TransferManager;

public class AbortMPUUsingHighLevelAPI {

    public static void main(String[] args) throws Exception {
        String existingBucketName = "cdc-dev-portal-2";
        
        TransferManager tm = new TransferManager(new ProfileCredentialsProvider());        

        int sevenDays = 1000 * 60 * 60 * 24 * 7;
		Date oneWeekAgo = new Date(System.currentTimeMillis() - sevenDays);
        
        try {
        	System.out.println("begin..");
        	tm.abortMultipartUploads(existingBucketName, oneWeekAgo);
        	System.out.println("end..");
        } catch (AmazonClientException amazonClientException) {
        	System.out.println("Unable to upload file, upload was aborted.");
        	amazonClientException.printStackTrace();
        }
    }
}

//程序不会自动终了