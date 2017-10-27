package aa.gao.aws.s3.object;

import java.io.IOException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class ListKeys {
	private static String bucketName = "cdc-dev-portal-2";
//	private static String bucketName = "nanbj-bucket";
	
	public static void main(String[] args) throws IOException {
        AmazonS3 s3client = new AmazonS3Client(new ProfileCredentialsProvider());
        try {
            System.out.println("Listing objects");
            final ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withMaxKeys(2);
            ListObjectsV2Result result;
            do {               
               result = s3client.listObjectsV2(req);
               
               for (S3ObjectSummary objectSummary : 
                   result.getObjectSummaries()) {
                   System.out.println(" - " + objectSummary.getKey() + "  " +
                           "(size = " + objectSummary.getSize() + 
                           ")");
               }
               System.out.println("Next Continuation Token : " + result.getNextContinuationToken());
               req.setContinuationToken(result.getNextContinuationToken());
            } while(result.isTruncated() == true ); 
            
         } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, " +
            		"which means your request made it " +
                    "to Amazon S3, but was rejected with an error response " +
                    "for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, " +
            		"which means the client encountered " +
                    "an internal error while trying to communicate" +
                    " with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }
}
/**
结果：

每个只列出了两个，应该是withMaxKey(2)的限制起作用了，即使是相同path目录里面的也是每两个取一次，相当于batchsize了

Listing objects
- aws-service-sample-0.0.1-SNAPSHOT.jar  (size = 9511773)
- csv/test.csv  (size = 94)
Next Continuation Token : 1loCWlte4Ol0M8OccR4BcOG1KN1UqAnEXXohc05JPyH2yv1TnNifBMA==
- images/sample.jpg  (size = 40415)
- mdm-cdc-dev-test/  (size = 0)
Next Continuation Token : 1GGtfPbo8L1a1nvTNuLUua0fS03OcQG+npU1mTURQJcsCZBydbA4qpTuXlnIAFOYR
- mdm-cdc-dev-test/test.csv  (size = 94)
- my-uploaded-pdf.pdf  (size = 108441)
Next Continuation Token : 1G6s0/bU1S24ac1D/SHsbBPqDtHtZLcpLF5RwuXlNWu9yYzPGvTsmiRcZ/u8C8wBo
- my-uploaded-pdf2.pdf  (size = 108441)
Next Continuation Token : null

注意点：
  bucketname为cdc-dev-portal-2，TIMEOUT了，难道是这个bucket里面的内容太多了？
  但是又执行了一次，这次就没有问题了
*/