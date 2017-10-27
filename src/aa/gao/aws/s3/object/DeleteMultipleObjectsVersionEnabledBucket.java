package aa.gao.aws.s3.object;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.DeleteObjectsResult.DeletedObject;
import com.amazonaws.services.s3.model.MultiObjectDeleteException;
import com.amazonaws.services.s3.model.MultiObjectDeleteException.DeleteError;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.SetBucketVersioningConfigurationRequest;

public class DeleteMultipleObjectsVersionEnabledBucket {

    static String bucketName = "cdc-dev-portal-3";
    static AmazonS3Client s3Client;

    public static void main(String[] args) throws IOException {

        try {
            s3Client = new AmazonS3Client(new ProfileCredentialsProvider());

            // 1. Enable versioning on the bucket.
            enableVersioningOnBucket(s3Client, bucketName);

            // 2a. Upload sample objects.
            List<KeyVersion> keysAndVersions1 = putObjects(3);
            // 2b. Delete specific object versions.
            multiObjectVersionedDelete(keysAndVersions1);

            // 3a. Upload samples objects. 
            List<KeyVersion> keysAndVersions2 = putObjects(3);
            // 3b. Delete objects using only keys. Amazon S3 creates a delete marker and 
            // returns its version Id in the response.            
            DeleteObjectsResult response = multiObjectNonVersionedDelete(keysAndVersions2);
            // 3c. Additional exercise - using multi-object versioned delete, remove the 
            // delete markers received in the preceding response. This results in your objects 
            // reappear in your bucket
            multiObjectVersionedDeleteRemoveDeleteMarkers(response);
            
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }

    static void enableVersioningOnBucket(AmazonS3Client s3Client,
            String bucketName) {
        BucketVersioningConfiguration config = new BucketVersioningConfiguration()
                .withStatus(BucketVersioningConfiguration.ENABLED);
        SetBucketVersioningConfigurationRequest setBucketVersioningConfigurationRequest = new SetBucketVersioningConfigurationRequest(
                bucketName, config);
        s3Client.setBucketVersioningConfiguration(setBucketVersioningConfigurationRequest);
    }

    static List<KeyVersion> putObjects(int number) {
        List<KeyVersion> keys = new ArrayList<KeyVersion>();
        String content = "This is the content body!";
        for (int i = 0; i < number; i++) {
            String key = "ObjectToDelete-" + new Random().nextInt();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setHeader("Subject", "Content-As-Object");
            metadata.setHeader("Content-Length", (long)content.length());
            PutObjectRequest request = new PutObjectRequest(bucketName, key,
                    new ByteArrayInputStream(content.getBytes()), metadata)
                    .withCannedAcl(CannedAccessControlList.AuthenticatedRead);
            PutObjectResult response = s3Client.putObject(request);
            KeyVersion keyVersion = new KeyVersion(key, response.getVersionId());
            keys.add(keyVersion);
        }
        return keys;
    }

    static void multiObjectVersionedDelete(List<KeyVersion> keys) {
        DeleteObjectsRequest multiObjectDeleteRequest = new DeleteObjectsRequest(
                bucketName).withKeys(keys);

        DeleteObjectsResult delObjRes = null;
        try {
            delObjRes = s3Client.deleteObjects(multiObjectDeleteRequest);
            System.out.format("Successfully deleted all the %s items.\n", delObjRes.getDeletedObjects().size());
        } catch(MultiObjectDeleteException mode) {
            printDeleteResults(mode);
        }
    }

    static DeleteObjectsResult multiObjectNonVersionedDelete(List<KeyVersion> keys) {

        // Multi-object delete by specifying only keys (no version ID).
        DeleteObjectsRequest multiObjectDeleteRequest = new DeleteObjectsRequest(
                bucketName);

        // Create request that include only object key names.
        List<KeyVersion> justKeys = new ArrayList<KeyVersion>();
        for (KeyVersion key : keys) {
            justKeys.add(new KeyVersion(key.getKey()));
        }

        multiObjectDeleteRequest.setKeys(justKeys);
        // Execute DeleteObjects - Amazon S3 add delete marker for each object
        // deletion. The objects no disappear from your bucket (verify).
        DeleteObjectsResult delObjRes = null;
        try {
            delObjRes = s3Client.deleteObjects(multiObjectDeleteRequest);
            System.out.format("Successfully deleted all the %s items.\n", delObjRes.getDeletedObjects().size());
        } catch (MultiObjectDeleteException mode) {
            printDeleteResults(mode);
        }
        return delObjRes;
    }
    static void multiObjectVersionedDeleteRemoveDeleteMarkers(
            DeleteObjectsResult response) {

        List<KeyVersion> keyVersionList = new ArrayList<KeyVersion>();
        for (DeletedObject deletedObject : response.getDeletedObjects()) {
            keyVersionList.add(new KeyVersion(deletedObject.getKey(),
                    deletedObject.getDeleteMarkerVersionId()));
        }
        // Create a request to delete the delete markers.
        DeleteObjectsRequest multiObjectDeleteRequest2 = new DeleteObjectsRequest(
                bucketName).withKeys(keyVersionList);

        // Now delete the delete marker bringing your objects back to the bucket.
        DeleteObjectsResult delObjRes = null;
        try {
            delObjRes = s3Client.deleteObjects(multiObjectDeleteRequest2);
            System.out.format("Successfully deleted all the %s items.\n", delObjRes.getDeletedObjects().size());
        } catch (MultiObjectDeleteException mode) {
            printDeleteResults(mode);
        }
    }
    static void printDeleteResults(MultiObjectDeleteException mode) {
        System.out.format("%s \n", mode.getMessage());
        System.out.format("No. of objects successfully deleted = %s\n", mode.getDeletedObjects().size());
        System.out.format("No. of objects failed to delete = %s\n", mode.getErrors().size());
        System.out.format("Printing error data...\n");
        for (DeleteError deleteError : mode.getErrors()){
            System.out.format("Object Key: %s\t%s\t%s\n", 
                    deleteError.getKey(), deleteError.getCode(), deleteError.getMessage());
        }
    }
}


/***
最后两个删除，一个是设定delete marker，一个是把delete marker再删除掉，这样本来看不到的对象就又可以重新看到了
*/