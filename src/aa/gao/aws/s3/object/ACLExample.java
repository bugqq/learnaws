package aa.gao.aws.s3.object;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CanonicalGrantee;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.Grant;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.Region;


public class ACLExample {
	private static String bucketName = "cdc-dev-portal-3";
	
	public static void main(String[] args) throws IOException {
        AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
        
        Collection<Grant> grantCollection = new ArrayList<Grant>();
        try {
            // 1. Create bucket with Canned ACL.
            CreateBucketRequest createBucketRequest = 
            	new CreateBucketRequest(bucketName, Region.AP_Tokyo).withCannedAcl(CannedAccessControlList.LogDeliveryWrite);  
            
            Bucket resp = s3Client.createBucket(createBucketRequest);

            // 2. Update ACL on the existing bucket.
            AccessControlList bucketAcl = s3Client.getBucketAcl(bucketName);
            
            if (bucketAcl != null) {
            	bucketAcl.getGrantsAsList().forEach(aa -> {
            		System.out.println(aa.getGrantee());
            		System.out.println(aa.getPermission()); });
            }
           
            
            // (Optional) delete all grants.
            bucketAcl.getGrants().clear();
            
            // Add grant - owner.
            Grant grant0 = new Grant(
            		new CanonicalGrantee("852b113e7a2f25102679df27bb0ae12b3f85be6f290b936c4393484beExample"), 
            		Permission.FullControl);
            grantCollection.add(grant0);     
            System.out.println("grant0 = " + grant0);
            
            // Add grant using canonical user id.
            Grant grant1 = new Grant(
            		new CanonicalGrantee("d25639fbe9c19cd30a4c0f43fbf00e2d3f96400a9aa8dabfbbebe1906Example"),
            		Permission.Write);        
            grantCollection.add(grant1);
            System.out.println("grant1 = " + grant1);
                       
            // Grant LogDelivery group permission to write to the bucket.
            Grant grant3 = new Grant(GroupGrantee.LogDelivery, 
            		                 Permission.Write);
            grantCollection.add(grant3);
            System.out.println("grant3 = " + grant3);
            
           bucketAcl.getGrants().addAll(grantCollection);

            // Save (replace) ACL.
            s3Client.setBucketAcl(bucketName, bucketAcl);
            
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
            ase.printStackTrace();
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means"+
            		" the client encountered " +
                    "a serious internal problem while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
            ace.printStackTrace();
        }
    }
}

/**

从执行结果可以看到创建完毕后，就已经有了缺省的ACL的Permission了
执行结果：
WARN : amazonaws.auth.profile.internal.BasicProfileConfigLoader:2017-10-24 15:56:17,982  - The legacy profile format requires the 'profile ' prefix before the profile name. The latest code does not require such prefix, and will consider it as part of the profile name. Please remove the prefix if you are seeing this warning.
com.amazonaws.services.s3.model.CanonicalGrantee@9f494219
FULL_CONTROL
GroupGrantee [http://acs.amazonaws.com/groups/s3/LogDelivery]
WRITE
GroupGrantee [http://acs.amazonaws.com/groups/s3/LogDelivery]
READ_ACP
grant0 = Grant [grantee=com.amazonaws.services.s3.model.CanonicalGrantee@4e10eb0c, permission=FULL_CONTROL]
grant1 = Grant [grantee=com.amazonaws.services.s3.model.CanonicalGrantee@bb4983df, permission=WRITE]
grant3 = Grant [grantee=GroupGrantee [http://acs.amazonaws.com/groups/s3/LogDelivery], permission=WRITE]
Caught an AmazonServiceException, which means your request made it to Amazon S3, but was rejected with an error response for some reason.
Error Message:    Invalid id (Service: Amazon S3; Status Code: 400; Error Code: InvalidArgument; Request ID: E306ACFE1D7BB11B; S3 Extended Request ID: efSFT2+VRYCVZi5NAnznRq/hjK/+KZDRcyFGD6WAqNS7NcaxgY9bZEjr09DkchA3jW1DhVxMVco=)
HTTP Status Code: 400
AWS Error Code:   InvalidArgument
Error Type:       Client
Request ID:       E306ACFE1D7BB11B
*/