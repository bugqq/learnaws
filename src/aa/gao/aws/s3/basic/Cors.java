package aa.gao.aws.s3.basic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.BucketCrossOriginConfiguration;
import com.amazonaws.services.s3.model.CORSRule;

public class Cors {

    /**
     * @param args
     * @throws IOException 
     */
    public static AmazonS3Client client;
    public static String bucketName = "cdc-dev-portal-5";
    
    public static void main(String[] args) throws IOException {
        client = new AmazonS3Client(new ProfileCredentialsProvider());
        client.setRegion(Region.getRegion(Regions.US_WEST_1));
        
        {
        	BucketCrossOriginConfiguration configuration = client.getBucketCrossOriginConfiguration(bucketName);
        	if (configuration == null) {
				System.out.println("Configuration is null");
			} else {
				printCORSConfiguration(configuration);
			}
        }

        // Create a new configuration request and add two rules
        BucketCrossOriginConfiguration configuration = new BucketCrossOriginConfiguration();
        
        List<CORSRule> rules = new ArrayList<CORSRule>();
        
        CORSRule rule1 = new CORSRule()
            .withId("CORSRule1")
            .withAllowedMethods(Arrays.asList(new CORSRule.AllowedMethods[] { 
                    CORSRule.AllowedMethods.PUT, CORSRule.AllowedMethods.POST, CORSRule.AllowedMethods.DELETE}))
            .withAllowedOrigins(Arrays.asList(new String[] {"http://*.example.com"}));
        
        CORSRule rule2 = new CORSRule()
        .withId("CORSRule2")
        .withAllowedMethods(Arrays.asList(new CORSRule.AllowedMethods[] { 
                CORSRule.AllowedMethods.GET}))
        .withAllowedOrigins(Arrays.asList(new String[] {"*"}))
        .withMaxAgeSeconds(3000)
        .withExposedHeaders(Arrays.asList(new String[] {"x-amz-server-side-encryption"}));
        
        configuration.setRules(Arrays.asList(new CORSRule[] {rule1, rule2}));
        
         // Add the configuration to the bucket. 
        client.setBucketCrossOriginConfiguration(bucketName, configuration);

        // Retrieve an existing configuration. 
        configuration = client.getBucketCrossOriginConfiguration(bucketName);
        printCORSConfiguration(configuration);
        
        // Add a new rule.
        CORSRule rule3 = new CORSRule()
        .withId("CORSRule3")
        .withAllowedMethods(Arrays.asList(new CORSRule.AllowedMethods[] { 
                CORSRule.AllowedMethods.HEAD}))
        .withAllowedOrigins(Arrays.asList(new String[] {"http://www.example.com"}));

        rules = configuration.getRules();
        rules.add(rule3);
        configuration.setRules(rules);
        client.setBucketCrossOriginConfiguration(bucketName, configuration);
        System.out.format("Added another rule: %s\n", rule3.getId());
        
        // Verify that the new rule was added.
        configuration = client.getBucketCrossOriginConfiguration(bucketName);
        System.out.format("Expected # of rules = 3, found %s", configuration.getRules().size());

        // Delete the configuration.
//        client.deleteBucketCrossOriginConfiguration(bucketName);
        
        // Try to retrieve configuration.
        configuration = client.getBucketCrossOriginConfiguration(bucketName);
        System.out.println("\nRemoved CORS configuration.");
        printCORSConfiguration(configuration);
    }
    
    static void printCORSConfiguration(BucketCrossOriginConfiguration configuration)
    {

        if (configuration == null)
        {
            System.out.println("\nConfiguration is null.");
            return;
        }

        System.out.format("\nConfiguration has %s rules:\n", configuration.getRules().size());
        for (CORSRule rule : configuration.getRules())
        {
            System.out.format("Rule ID: %s\n", rule.getId());
            System.out.format("MaxAgeSeconds: %s\n", rule.getMaxAgeSeconds());
            System.out.format("AllowedMethod: %s\n", rule.getAllowedMethods().toArray());
            System.out.format("AllowedOrigins: %s\n", rule.getAllowedOrigins());
            System.out.format("AllowedHeaders: %s\n", rule.getAllowedHeaders());
            System.out.format("ExposeHeader: %s\n", rule.getExposedHeaders());
        }
    }
}

/**
运行结果：


Configuration has 3 rules:
Rule ID: CORSRule1
MaxAgeSeconds: 0
AllowedMethod: PUT
AllowedOrigins: [http://*.example.com]
AllowedHeaders: null
ExposeHeader: null
Rule ID: CORSRule2
MaxAgeSeconds: 3000
AllowedMethod: GET
AllowedOrigins: [*]
AllowedHeaders: null
ExposeHeader: [x-amz-server-side-encryption]
Rule ID: CORSRule3
MaxAgeSeconds: 0
AllowedMethod: HEAD
AllowedOrigins: [http://www.example.com]
AllowedHeaders: null
ExposeHeader: null

Configuration has 2 rules:
Rule ID: CORSRule1
MaxAgeSeconds: 0
AllowedMethod: PUT
AllowedOrigins: [http://*.example.com]
AllowedHeaders: null
ExposeHeader: null
Rule ID: CORSRule2
MaxAgeSeconds: 3000
AllowedMethod: GET
AllowedOrigins: [*]
AllowedHeaders: null
ExposeHeader: [x-amz-server-side-encryption]
Added another rule: CORSRule3
Expected # of rules = 3, found 3
Removed CORS configuration.

Configuration has 3 rules:
Rule ID: CORSRule1
MaxAgeSeconds: 0
AllowedMethod: PUT
AllowedOrigins: [http://*.example.com]
AllowedHeaders: null
ExposeHeader: null
Rule ID: CORSRule2
MaxAgeSeconds: 3000
AllowedMethod: GET
AllowedOrigins: [*]
AllowedHeaders: null
ExposeHeader: [x-amz-server-side-encryption]
Rule ID: CORSRule3
MaxAgeSeconds: 0
AllowedMethod: HEAD
AllowedOrigins: [http://www.example.com]
AllowedHeaders: null
ExposeHeader: null


*/