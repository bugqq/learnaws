package aa.gao.aws.s3.basic;

import java.io.IOException;
import java.net.URL;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

public class GeneratePreSignedUrl {
	private static String bucketName = "cdc-dev-portal-5";
	private static String objectKey  =  "glacierobjects/helloworld";

	public static void main(String[] args) throws IOException {
		AmazonS3 s3client = new AmazonS3Client(new ProfileCredentialsProvider());
		s3client.setRegion(Region.getRegion(Regions.US_WEST_1));

		try {
			System.out.println("Generating pre-signed URL.");
			java.util.Date expiration = new java.util.Date();
			long milliSeconds = expiration.getTime();
			milliSeconds += 1000 * 60 * 60; // Add 1 hour.
			expiration.setTime(milliSeconds);

			GeneratePresignedUrlRequest generatePresignedUrlRequest = 
				    new GeneratePresignedUrlRequest(bucketName, objectKey);
			generatePresignedUrlRequest.setMethod(HttpMethod.GET); 
			generatePresignedUrlRequest.setExpiration(expiration);

			URL url = s3client.generatePresignedUrl(generatePresignedUrlRequest);
			System.out.println("Pre-Signed URL Class Name = " + url.getClass().getName());

			System.out.println("Pre-Signed URL = " + url.toString());
		} catch (AmazonServiceException exception) {
			System.out.println("Caught an AmazonServiceException, " +
					"which means your request made it " +
					"to Amazon S3, but was rejected with an error response " +
			"for some reason.");
			System.out.println("Error Message: " + exception.getMessage());
			System.out.println("HTTP  Code: "    + exception.getStatusCode());
			System.out.println("AWS Error Code:" + exception.getErrorCode());
			System.out.println("Error Type:    " + exception.getErrorType());
			System.out.println("Request ID:    " + exception.getRequestId());
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
/*
运行结果：
Generating pre-signed URL.
Pre-Signed URL Class Name = java.net.URL
Pre-Signed URL = https://cdc-dev-portal-5.s3-us-west-1.amazonaws.com/glacierobjects/helloworld?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20171018T105524Z&X-Amz-SignedHeaders=host&X-Amz-Expires=3599&X-Amz-Credential=xxxxxxxxxxxxxx%2F20171018%2Fus-west-1%2Fs3%2Faws4_request&X-Amz-Signature=be92136f815fafb587821c3c8144dbf2cf632fee36b80baf4dfb88c1788c6fb4
*/