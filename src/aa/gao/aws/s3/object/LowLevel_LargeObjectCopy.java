package aa.gao.aws.s3.object;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;

public class LowLevel_LargeObjectCopy {

    public static void main(String[] args) throws IOException {
    	
        String sourceBucketName = "cdc-dev-portal-2";
        String targetBucketName = "cdc-dev-portal-2";
        String sourceObjectKey  = "test/lowerlevel/testlog.log";
        String targetObjectKey  = "test/lowerlevel-copy/testlog-copy.log";
        AmazonS3Client s3Client = new AmazonS3Client(new 
        		PropertiesCredentials(
        				LowLevel_LargeObjectCopy.class.getResourceAsStream(
        						"AwsCredentials.properties")));    
        
        // List to store copy part responses.

        List<CopyPartResult> copyResponses =
                  new ArrayList<CopyPartResult>();
                          
        InitiateMultipartUploadRequest initiateRequest = 
        	new InitiateMultipartUploadRequest(targetBucketName, targetObjectKey);
        
        InitiateMultipartUploadResult initResult = 
        	s3Client.initiateMultipartUpload(initiateRequest);

        try {
            // Get object size.
            GetObjectMetadataRequest metadataRequest = 
            	new GetObjectMetadataRequest(sourceBucketName, sourceObjectKey);

            ObjectMetadata metadataResult = s3Client.getObjectMetadata(metadataRequest);
            long objectSize = metadataResult.getContentLength(); // in bytes

            // Copy parts.
            long partSize = 5 * (long)Math.pow(2.0, 20.0); // 5 MB

            long bytePosition = 0;
            for (int i = 1; bytePosition < objectSize; i++)
            {
            	CopyPartRequest copyRequest = new CopyPartRequest()
                   .withDestinationBucketName(targetBucketName)
                   .withDestinationKey(targetObjectKey)
                   .withSourceBucketName(sourceBucketName)
                   .withSourceKey(sourceObjectKey)
                   .withUploadId(initResult.getUploadId())
                   .withFirstByte(bytePosition)
                   .withLastByte(bytePosition + partSize -1 >= objectSize ? objectSize - 1 : bytePosition + partSize - 1) 
                   .withPartNumber(i);

                copyResponses.add(s3Client.copyPart(copyRequest));
                bytePosition += partSize;

            }
            CompleteMultipartUploadRequest completeRequest = new 
            	CompleteMultipartUploadRequest(
            			targetBucketName,
            			targetObjectKey,
            			initResult.getUploadId(),
            			GetETags(copyResponses));

            CompleteMultipartUploadResult completeUploadResponse =
                s3Client.completeMultipartUpload(completeRequest);
        } catch (Exception e) {
        	System.out.println(e.getMessage());
        }
     }
     
    // Helper function that constructs ETags.
    static List<PartETag> GetETags(List<CopyPartResult> responses)
    {
        List<PartETag> etags = new ArrayList<PartETag>();
        for (CopyPartResult response : responses)
        {
            etags.add(new PartETag(response.getPartNumber(), response.getETag()));
        }
        return etags;
    }   
}

/**
说明：
1）忘记加log了， 正常执行终了

2）不过这个需要指定properties文件，和这个java文件在同一个目录即可
内容如下
[default]
accessKey = xxxxxxxxxxxxxxxxxxx
secretKey = xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

这个文件是从C:\Users\yanpeng.gao\.aws目录下面的credentials文件拷贝过来的。
文件内容为：
[default]
aws_access_key_id = xxxxxxxxxxxxx
aws_secret_access_key = xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

要注意key不一样啊

目录下面还有一个文件config文件内容是
[default]
region = ap-northeast-1

3）这个执行的时候就没有出现TIMEOUT的问题，即使是一个19M的文件，可以和CopyObjectSingleOperation.java文件里面执行后的结果对比参照一下

*/