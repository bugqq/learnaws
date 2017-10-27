package aa.gao.aws.s3.basic;

import java.io.IOException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.BucketWebsiteConfiguration;



public class WebsiteConfiguration {
//	private static String bucketName = "*** bucket name ***";
//	private static String indexDoc   = "*** index document name ***";
//	private static String errorDoc   = "*** error document name ***";
	private static String bucketName = "cdc-dev-portal-6";
	private static String indexDoc   = "index.html";
	private static String errorDoc   = "error.html";
	
	public static void main(String[] args) throws IOException {
        AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
   
        try {
        	// Get existing website configuration, if any.
            BucketWebsiteConfiguration websiteConfigOld = getWebsiteConfig(s3Client);
            System.out.println("original website config is " + websiteConfigOld);
    		
    		// Set new website configuration.
    		s3Client.setBucketWebsiteConfiguration(bucketName, 
    		   new BucketWebsiteConfiguration(indexDoc, errorDoc));
    		
    		// Verify (Get website configuration again).
            BucketWebsiteConfiguration websiteConfigNew = getWebsiteConfig(s3Client);
            System.out.println("new website config is " + websiteConfigNew);
            
            // Delete
            s3Client.deleteBucketWebsiteConfiguration(bucketName);

       		// Verify (Get website configuration again)
			BucketWebsiteConfiguration websiteConfigAfterDel = getWebsiteConfig(s3Client);
			System.out.println("after delete website config is " + websiteConfigAfterDel);
            
  
            
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which" +
            		" means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means"+
            		" the client encountered " +
                    "a serious internal problem while trying to " +
                    "communicate with Amazon S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }

	private static BucketWebsiteConfiguration getWebsiteConfig(
	                                              AmazonS3 s3Client) {
		System.out.println("Get website config");   
		
		// 1. Get website config.
		BucketWebsiteConfiguration bucketWebsiteConfiguration = 
			  s3Client.getBucketWebsiteConfiguration(bucketName);
		if (bucketWebsiteConfiguration == null)
		{
			System.out.println("No website config.");
		}
		else
		{
		     System.out.println("Index doc:" + 
		       bucketWebsiteConfiguration.getIndexDocumentSuffix());
		     System.out.println("Error doc:" + 
		       bucketWebsiteConfiguration.getErrorDocument());
		}
		return bucketWebsiteConfiguration;
	}
}

/**
 * 
 * 
 前提条件
 1）上传index.html和error.html
 2）设定 Bucket Policy Permission 如下
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "PublicReadGetObject",
            "Effect": "Allow",
            "Principal": "*",
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::cdc-dev-portal-6/*"
        }
    ]
}
 
 */

/**

1）
运行结果，如果设定了index 的场合，
访问 http://cdc-dev-portal-6.s3-website-us-west-1.amazonaws.com/
会出现

具体的网站内容啊 （自动导航到了.html文件

否则会出现
404 Not Found

Code: NoSuchWebsiteConfiguration
Message: The specified bucket does not have a website configuration
BucketName: cdc-dev-portal-6
RequestId: B51C0BC58B99F75D
HostId: h246Xf1siy2zK16xk7dsYik9gKGmqUg4HuE884pAvHRuwtGYLlp8ZK8Lis51SzWCaoxgUQtHF6s=


2）
如果不设定Permission的Bucket Policy
则出现
403 Forbidden
•Code: AccessDenied
•Message: Access Denied
•RequestId: C07D8420B41C901F
•HostId: 16AQEaD7hcNM58VQF6vaeEcC18ILYND364280HLSq0lM2q0AB8WCxr9p5qudQ2NikFjI10efKsw=

An Error Occurred While Attempting to Retrieve a Custom Error Document
•Code: AccessDenied
•Message: Access Denied

*/