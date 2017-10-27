package -- basic
1. CreateBucket.java -- 创建bucket
2. DeleteBucket.java -- 删除内容和bucket 
3. WebsiteConfiguration.java -- 启用和指定Website的页面
4. BucketAccelerationConfiguration.java -- 启用加速设定
5. AcceleratedUploadSingleObject.java -- 加速方式上传文件
6. AcceleratedMultipartUploadUsingHighLevelAPI.java -- 异步加速方式上传文件
7. PutObject.java -- 正常上传文件
8. LifecycleConfiguraton.java -- 配置Lifecycle为了bucket，设置过期时间转换或者删除
9. Cors.java -- 实现跨源资源共享 (CORS)
10. GetObject.java -- 获取对象
11. GeneratePreSignedUrl.java -- 生成预签名对象 URL
12. UploadObjectSingleOperation.java -- 上传单个文件
12-1. ObjectTaggingTest.java -- 对象标签操作

images -- s3accelerated -- 图片 for AcceleratedUploadSingleObject.java 加速方式上传文件

package --  multi
13. UploadObjectMultipartUploadUsingHighLevelAPI-- 分段上传之上传文件
14. AbortMPUUsingHighLevelAPI.java -- 分段上传之分段上传中止过程
15. TrackMPUProgressUsingHighLevelAPI.java-- 跟踪分段上传进度
16. UploadObjectMPULowLevelAPI.java -- 低级别 API 文件上传过程
17. GeneratePresignedUrlAndUploadObject.java -- 使用预签名 URL 上传对象

package -- object
18. CopyObjectSingleOperation.java -- 复制对象
19. LowLevel_LargeObjectCopy.java -- 使用AWS SDK for Java分段上传 API 复制对象
20. ListKeys.java -- 使用前缀和分隔符按层次结构列出键

21. DeleteAnObjectNonVersionedBucket.java -- 删除对象 (不受版本控制的存储桶)
22. DeleteAnObjectVersionEnabledBucket.java -- 删除对象 (受版本控制的存储桶)
 1）.对存储桶启用版本控制功能。
 2）.向存储桶添加示例对象。作为响应，Amazon S3 将返回新添加的对象的版本 ID。
 3）.使用 deleteVersion 方法删除示例对象。DeleteVersionRequest 类将指定对象键名称和版本 ID。 
23.DeleteMultipleObjectsNonVersionedBucket.java -- 多对象删除 (不受版本控制的存储桶)
24.DeleteMultipleObjectsVersionEnabledBucket.java -- 多对象删除 (启用了版本的存储桶)

25.RestoreArchivedObject.java -- 使用 AWS SDK for Java 还原存档对象
26.ACLExample.java -- 使用AWS SDK for Java管理 ACL

27.ServerSideEncryptionUsingClientSideEncryptionKey.java -- 使用 AWS Java 开发工具包指定具有客户提供的加密密钥的服务器端加密
28.  示例 1：使用客户端对称主密钥加密和上传文件
 28.1) GenerateSymmetricMasterKey.java -- 示例 1a：创建对称主密钥
 28.2) S3ClientSideEncryptionWithSymmetricMasterKey.java -- 示例 1b：使用对称密钥将一个文件上传到 Amazon S3
29.
 
images -- Image4DeleteAnObjectVersionEnabled -- 图片 for DeleteAnObjectVersionEnabledBucket.java 删除对象 (受版本控制的存储桶)
 
 