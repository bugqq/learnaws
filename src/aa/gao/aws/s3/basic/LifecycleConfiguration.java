package aa.gao.aws.s3.basic;

import java.io.IOException;
import java.util.Arrays;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration.Transition;
import com.amazonaws.services.s3.model.StorageClass;
import com.amazonaws.services.s3.model.Tag;
import com.amazonaws.services.s3.model.lifecycle.LifecycleAndOperator;
import com.amazonaws.services.s3.model.lifecycle.LifecycleFilter;
import com.amazonaws.services.s3.model.lifecycle.LifecyclePrefixPredicate;
import com.amazonaws.services.s3.model.lifecycle.LifecycleTagPredicate;
import com.sybase.jdbc4.a.b.am;

public class LifecycleConfiguration {
    public static String bucketName = "cdc-dev-portal-5";
    public static AmazonS3Client s3Client;

    public static void main(String[] args) throws IOException {
        s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
        s3Client.setRegion(Region.getRegion(Regions.US_WEST_1));
        try {
        	{
				BucketLifecycleConfiguration configuration = s3Client.getBucketLifecycleConfiguration(bucketName);
				if (configuration == null) {
					System.out.println("Configuration is null");
				} else {
					System.out.format("Expected # of rules = 3; found: %s\n", configuration.getRules().size());
					configuration.getRules().forEach(xxx -> System.out.println("id=" + xxx.getId() + " detail=" + xxx));
				}
        	}

            BucketLifecycleConfiguration.Rule rule1 =
            new BucketLifecycleConfiguration.Rule()
            .withId("Archive immediately rule")
            .withFilter(new LifecycleFilter(
                    new LifecyclePrefixPredicate("glacierobjects/")))
            .addTransition(new Transition()
                    .withDays(0)
                    .withStorageClass(StorageClass.Glacier))
            .withStatus(BucketLifecycleConfiguration.ENABLED.toString());

            BucketLifecycleConfiguration.Rule rule2 =
                new BucketLifecycleConfiguration.Rule()
                .withId("Archive and then delete rule")
                .withFilter(new LifecycleFilter(
                        new LifecycleTagPredicate(new Tag("archive", "true"))))
                .addTransition(new Transition()
                        .withDays(30)
                        .withStorageClass(StorageClass.StandardInfrequentAccess))
                .addTransition(new Transition()
                        .withDays(365)
                        .withStorageClass(StorageClass.Glacier))
                .withExpirationInDays(3650)
                .withStatus(BucketLifecycleConfiguration.ENABLED.toString());

            BucketLifecycleConfiguration configuration =
            new BucketLifecycleConfiguration()
                .withRules(Arrays.asList(rule1, rule2));

            // Save configuration.
            s3Client.setBucketLifecycleConfiguration(bucketName, configuration);

            // Retrieve configuration.
            configuration = s3Client.getBucketLifecycleConfiguration(bucketName);

            // Add a new rule.
            configuration.getRules().add(
                new BucketLifecycleConfiguration.Rule()
                    .withId("NewRule")
                    .withFilter(new LifecycleFilter(
                        new LifecycleAndOperator(Arrays.asList(
                            new LifecyclePrefixPredicate("YearlyDocuments/"),
                            new LifecycleTagPredicate(new Tag("expire_after", "ten_years"))))))
                    .withExpirationInDays(3650)
                    .withStatus(BucketLifecycleConfiguration.
                        ENABLED.toString())
                );

            // Save configuration.
            s3Client.setBucketLifecycleConfiguration(bucketName, configuration);

            // Retrieve configuration.
            configuration = s3Client.getBucketLifecycleConfiguration(bucketName);

            // Verify there are now three rules.
            configuration = s3Client.getBucketLifecycleConfiguration(bucketName);
            System.out.format("Expected # of rules = 3; found: %s\n",
                configuration.getRules().size());
            
            configuration.getRules().forEach(xxx -> System.out.println("id="+ xxx.getId() + " detail=" +xxx));

            System.out.println("Deleting lifecycle configuration. Next, we verify deletion.");
            // Delete configuration.
//            s3Client.deleteBucketLifecycleConfiguration(bucketName);

            // Retrieve nonexistent configuration.
            configuration = s3Client.getBucketLifecycleConfiguration(bucketName);
            String s = (configuration == null) ? "No configuration found." : "Configuration found.";
            System.out.println(s);

        } catch (AmazonS3Exception amazonS3Exception) {
            System.out.format("An Amazon S3 error occurred. Exception: %s", amazonS3Exception.toString());
            amazonS3Exception.printStackTrace();
        } catch (Exception ex) {
            System.out.format("Exception: %s", ex.toString());
            ex.printStackTrace();
        }
    }
}
/**
运行结果：
Expected # of rules = 3; found: 3
id=Archive immediately rule detail=com.amazonaws.services.s3.model.BucketLifecycleConfiguration$Rule@6e950bcf
id=Archive and then delete rule detail=com.amazonaws.services.s3.model.BucketLifecycleConfiguration$Rule@16414e40
id=NewRule detail=com.amazonaws.services.s3.model.BucketLifecycleConfiguration$Rule@74bada02
Expected # of rules = 3; found: 3
id=Archive immediately rule detail=com.amazonaws.services.s3.model.BucketLifecycleConfiguration$Rule@7cbc3762
id=Archive and then delete rule detail=com.amazonaws.services.s3.model.BucketLifecycleConfiguration$Rule@3a6f2de3
id=NewRule detail=com.amazonaws.services.s3.model.BucketLifecycleConfiguration$Rule@49872d67
Deleting lifecycle configuration. Next, we verify deletion.
Configuration found.
*/