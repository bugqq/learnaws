package aa.gao.aws.s3.multi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;

public class UploadObjectMPULowLevelAPI {

    public static void main(String[] args) throws IOException {
//        String existingBucketName  = "*** Provide-Your-Existing-BucketName ***"; 
//        String keyName             = "*** Provide-Key-Name ***";
//        String filePath            = "*** Provide-File-Path ***";
        
        String existingBucketName = "cdc-dev-portal-2";
        String keyName            = "test/lowerlevel/testlog.log";
        String filePath           = "C:\\tmp\\tomcat8\\mdm\\online.log.2017-10-19";  

        
        AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());        

        // Create a list of UploadPartResponse objects. You get one of these
        // for each part upload.
        List<PartETag> partETags = new ArrayList<PartETag>();

        // Step 1: Initialize.
        InitiateMultipartUploadRequest initRequest = new 
             InitiateMultipartUploadRequest(existingBucketName, keyName);
        InitiateMultipartUploadResult initResponse = 
        	                   s3Client.initiateMultipartUpload(initRequest);

        File file = new File(filePath);
        long contentLength = file.length();
        System.out.println("contentLength = " + contentLength);
        long partSize = 5242880; // Set part size to 5 MB.
        System.out.println("partSize = " + partSize);

        try {
            // Step 2: Upload parts.
            long filePosition = 0;
            for (int i = 1; filePosition < contentLength; i++) {
            	System.out.println("filePosition = " + filePosition);
            	
                // Last part can be less than 5 MB. Adjust part size.
            	partSize = Math.min(partSize, (contentLength - filePosition));
            	
                // Create request to upload a part.
                UploadPartRequest uploadRequest = new UploadPartRequest()
                    .withBucketName(existingBucketName).withKey(keyName)
                    .withUploadId(initResponse.getUploadId()).withPartNumber(i)
                    .withFileOffset(filePosition)
                    .withFile(file)
                    .withPartSize(partSize);

                // Upload part and add response to our list.
                partETags.add(
                		s3Client.uploadPart(uploadRequest).getPartETag());

                System.out.println("partETags = " + partETags);
                filePosition += partSize;
            }

            // Step 3: Complete.
            CompleteMultipartUploadRequest compRequest = new 
                         CompleteMultipartUploadRequest(
                                    existingBucketName, 
                                    keyName, 
                                    initResponse.getUploadId(), 
                                    partETags);

            System.out.println("before upload complete");
            s3Client.completeMultipartUpload(compRequest);
            System.out.println("after upload complete");
        } catch (Exception e) {
            s3Client.abortMultipartUpload(new AbortMultipartUploadRequest(
                    existingBucketName, keyName, initResponse.getUploadId()));
        }
    }
}
/**
执行结果
contentLength = 19981351
partSize = 5242880
filePosition = 0
partETags = [com.amazonaws.services.s3.model.PartETag@22d7b4f8]
filePosition = 5242880
partETags = [com.amazonaws.services.s3.model.PartETag@22d7b4f8, com.amazonaws.services.s3.model.PartETag@38831718]
filePosition = 10485760
partETags = [com.amazonaws.services.s3.model.PartETag@22d7b4f8, com.amazonaws.services.s3.model.PartETag@38831718, com.amazonaws.services.s3.model.PartETag@33fe57a9]
filePosition = 15728640
partETags = [com.amazonaws.services.s3.model.PartETag@22d7b4f8, com.amazonaws.services.s3.model.PartETag@38831718, com.amazonaws.services.s3.model.PartETag@33fe57a9, com.amazonaws.services.s3.model.PartETag@691939c9]
before upload complete
after upload complete
*/