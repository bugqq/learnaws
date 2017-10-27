package aa.gao.aws.s3.basic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;

public class ObjectTaggingTest {
    
//    static String bucketName = "***bucket***";
//    static String keyName = "***object key name***";
//    static String filePath = "***filepath***";
    
	static String bucketName     = "cdc-dev-portal-5";
	static String keyName        = "helloworld";
	static String filePath = "C:\\TEMP\\test.txt";


    public static void main(String[] args) {

    	
//        AmazonS3Client s3client = new AmazonS3Client(new BasicAWSCredentials("<AccessKey>", "<SecretKey>"));
    	String accessKey = "xxxxxxxxxxxxxxxx";
		String secretKey = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
		AmazonS3Client s3client = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
		s3client.setRegion(Region.getRegion(Regions.US_WEST_1));

        // 1. Put object with tags.
        PutObjectRequest putRequest = new PutObjectRequest(bucketName, keyName, new File(filePath)); 
        List<Tag> tags = new ArrayList<Tag>();
        tags.add(new Tag("Key1", "Value1"));
        tags.add(new Tag("Key2", "Value2"));
        putRequest.setTagging(new ObjectTagging(tags));
        PutObjectResult putResult = s3client.putObject(putRequest);
        
        // 2. Retrieve object tags.
        GetObjectTaggingRequest getTaggingRequest = new GetObjectTaggingRequest(bucketName, keyName);
        GetObjectTaggingResult  getTagsResult = s3client.getObjectTagging(getTaggingRequest);
        
        System.out.println("tag is ------------------------");
        getTagsResult.getTagSet().forEach(xxx -> System.out.println(xxx.getKey() + "="+ xxx.getValue()));
        
        // 3. Replace the tagset.
        List<Tag> newTags = new ArrayList<Tag>();
        newTags.add(new Tag("Key3", "Value3"));
        newTags.add(new Tag("Key4", "Value4"));
        s3client.setObjectTagging(new SetObjectTaggingRequest(bucketName, keyName, new ObjectTagging(newTags)));

        // 4. Retrieve object tags.
        GetObjectTaggingRequest getTaggingRequest2 = new GetObjectTaggingRequest(bucketName, keyName);
        GetObjectTaggingResult  getTagsResult2 = s3client.getObjectTagging(getTaggingRequest);
        
        System.out.println("replace tag is ------------------------");
        getTagsResult2.getTagSet().forEach(xxx -> System.out.println(xxx.getKey() + "="+ xxx.getValue()));
    }
}

/**

创建带标签的对象。
检索标签集。
更新标签集 (替换现有标签集)。

执行结果：
tag is ------------------------
Key2=Value2
Key1=Value1
replace tag is ------------------------
Key4=Value4
Key3=Value3

注意：
不指定 s3client.setRegion(Region.getRegion(Regions.US_WEST_1));
就会出现timeout 连接不上的错误啊 

然后在s3的portal上面就可以确认是增加了2个tag了
*/