package aa.gao.aws.s3.object;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
//import org.junit.Assert;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3EncryptionClient;
import com.amazonaws.services.s3.model.EncryptionMaterials;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.StaticEncryptionMaterialsProvider;
import com.amazonaws.services.s3.model.VersionListing;

public class S3ClientSideEncryptionWithSymmetricMasterKey {
    private static final String masterKeyDir = System.getProperty("java.io.tmpdir");
//    private static final String bucketName = UUID.randomUUID() + "-"
//            + DateTimeFormat.forPattern("yyMMdd-hhmmss").print(new DateTime());
//    private static final String objectKey = UUID.randomUUID().toString();
    
    private static final String bucketName = "cdc-dev-portal-3";
    private static final String objectKey = "test/test222.log";

    public static void main(String[] args) throws Exception {
    	/**
        SecretKey mySymmetricKey = GenerateSymmetricMasterKey
                .loadSymmetricAESKey(masterKeyDir, "AES");

        EncryptionMaterials encryptionMaterials = new EncryptionMaterials(
                mySymmetricKey);

        AmazonS3EncryptionClient encryptionClient = new AmazonS3EncryptionClient(
                new ProfileCredentialsProvider(),
                new StaticEncryptionMaterialsProvider(encryptionMaterials));
        // Create the bucket
        encryptionClient.withRegion(Regions.AP_NORTHEAST_1);
        encryptionClient.createBucket(bucketName);
        

        // Upload object using the encryption client.
        byte[] plaintext = "Hello World, S3 Client-side Encryption Using Asymmetric Master Key!"
                .getBytes();
        System.out.println("plaintext's length: " + plaintext.length);
        encryptionClient.putObject(new PutObjectRequest(bucketName, objectKey,
                new ByteArrayInputStream(plaintext), new ObjectMetadata()));

        // Download the object.
        S3Object downloadedObject = encryptionClient.getObject(bucketName,
                objectKey);
        byte[] decrypted = IOUtils.toByteArray(downloadedObject
                .getObjectContent());
        
        // Verify same data.
//        Assert.assertTrue(Arrays.equals(plaintext, decrypted));
        deleteBucketAndAllContents(encryptionClient);
        */
    }

    private static void deleteBucketAndAllContents(AmazonS3 client) {
        System.out.println("Deleting S3 bucket: " + bucketName);
        ObjectListing objectListing = client.listObjects(bucketName);

        while (true) {
            for ( Iterator<?> iterator = objectListing.getObjectSummaries().iterator(); iterator.hasNext(); ) {
                S3ObjectSummary objectSummary = (S3ObjectSummary) iterator.next();
                client.deleteObject(bucketName, objectSummary.getKey());
            }

            if (objectListing.isTruncated()) {
                objectListing = client.listNextBatchOfObjects(objectListing);
            } else {
                break;
            }
        };
        VersionListing list = client.listVersions(new ListVersionsRequest().withBucketName(bucketName));
        for ( Iterator<?> iterator = list.getVersionSummaries().iterator(); iterator.hasNext(); ) {
            S3VersionSummary s = (S3VersionSummary)iterator.next();
            client.deleteVersion(bucketName, s.getKey(), s.getVersionId());
        }
        client.deleteBucket(bucketName);
    }
}

/**
 * 
执行出现错误，如下结果, 原因不明
WARN : amazonaws.auth.profile.internal.BasicProfileConfigLoader:2017-10-24 16:48:31,454  - The legacy profile format requires the 'profile ' prefix before the profile name. The latest code does not require such prefix, and will consider it as part of the profile name. Please remove the prefix if you are seeing this warning.
plaintext's length: 67
Exception in thread "main" com.amazonaws.AmazonClientException: Unable to encrypt symmetric key
	at com.amazonaws.util.Throwables.failure(Throwables.java:74)
	at com.amazonaws.services.s3.internal.crypto.ContentCryptoMaterial.secureCEK(ContentCryptoMaterial.java:896)
	at com.amazonaws.services.s3.internal.crypto.ContentCryptoMaterial.doCreate(ContentCryptoMaterial.java:808)
	at com.amazonaws.services.s3.internal.crypto.ContentCryptoMaterial.create(ContentCryptoMaterial.java:768)
	at com.amazonaws.services.s3.internal.crypto.S3CryptoModuleBase.buildContentCryptoMaterial(S3CryptoModuleBase.java:549)
	at com.amazonaws.services.s3.internal.crypto.S3CryptoModuleBase.newContentCryptoMaterial(S3CryptoModuleBase.java:490)
	at com.amazonaws.services.s3.internal.crypto.S3CryptoModuleBase.createContentCryptoMaterial(S3CryptoModuleBase.java:456)
	at com.amazonaws.services.s3.internal.crypto.S3CryptoModuleBase.putObjectUsingMetadata(S3CryptoModuleBase.java:165)
	at com.amazonaws.services.s3.internal.crypto.S3CryptoModuleBase.putObjectSecurely(S3CryptoModuleBase.java:161)
	at com.amazonaws.services.s3.internal.crypto.CryptoModuleDispatcher.putObjectSecurely(CryptoModuleDispatcher.java:108)
	at com.amazonaws.services.s3.AmazonS3EncryptionClient.putObject(AmazonS3EncryptionClient.java:570)
	at aa.gao.aws.object.S3ClientSideEncryptionWithSymmetricMasterKey.main(S3ClientSideEncryptionWithSymmetricMasterKey.java:58)
Caused by: java.security.InvalidKeyException: Illegal key size or default parameters
	at javax.crypto.Cipher.checkCryptoPerm(Cipher.java:1026)
	at javax.crypto.Cipher.implInit(Cipher.java:801)
	at javax.crypto.Cipher.chooseProvider(Cipher.java:864)
	at javax.crypto.Cipher.init(Cipher.java:1249)
	at javax.crypto.Cipher.init(Cipher.java:1186)
	at com.amazonaws.services.s3.internal.crypto.ContentCryptoMaterial.secureCEK(ContentCryptoMaterial.java:893)
	... 10 more
*/