package aa.gao.aws.s3.basic;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.VersionListing;
import java.util.Iterator;

/**
 * 
1）初期化的一种用法，但是被deprecated了
final AmazonS3 s3 = new AmazonS3Client();
2）警告，会删除所有bucket的数据，要注意启动参数
*/

/**
 * Delete an Amazon S3 bucket.
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 *
 * ++ Warning ++ This code will actually delete the bucket that you specify, as
 *               well as any objects within it!
 */
public class DeleteBucket
{
    public static void main(String[] args)
    {
        final String USAGE = "\n" +
            "To run this example, supply the name of an S3 bucket\n" +
            "\n" +
            "Ex: DeleteBucket <bucketname>\n";

        args = new String[]{"cdc-dev-portal-3"};
        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String bucket_name = args[0];

        System.out.println("Deleting S3 bucket: " + bucket_name);
        final AmazonS3 s3 = new AmazonS3Client();

        try {
            System.out.println(" - removing objects from bucket");
            ObjectListing object_listing = s3.listObjects(bucket_name);
            while (true) {
                for (Iterator<?> iterator =
                        object_listing.getObjectSummaries().iterator();
                        iterator.hasNext();) {
                    S3ObjectSummary summary = (S3ObjectSummary)iterator.next();
                    System.out.println(" - removing objects from bucket" + " bucket_name=" + bucket_name + " key="+summary.getKey());
                    s3.deleteObject(bucket_name, summary.getKey());
                }

                // more object_listing to retrieve?
                if (object_listing.isTruncated()) {
                	System.out.println(" - removing objects from object" + " isTruncated list next batch");
                    object_listing = s3.listNextBatchOfObjects(object_listing);
                } else {
                    break;
                }
            };

            System.out.println(" - removing versions from bucket");
            VersionListing version_listing = s3.listVersions(
                    new ListVersionsRequest().withBucketName(bucket_name));
            while (true) {
                for (Iterator<?> iterator =
                        version_listing.getVersionSummaries().iterator();
                        iterator.hasNext();) {
                    S3VersionSummary vs = (S3VersionSummary)iterator.next();
                    System.out.println(" - removing objects from version" + " bucket_name=" + bucket_name + " key="+vs.getKey()+" versionId="+vs.getVersionId());
                    s3.deleteVersion(
          
                  bucket_name, vs.getKey(), vs.getVersionId());
                }

                if (version_listing.isTruncated()) {
                	System.out.println(" - removing objects from version" + " isTruncated list next batch");
                    version_listing = s3.listNextBatchOfVersions(
                            version_listing);
                } else {
                    break;
                }
            }

            System.out.println(" OK, bucket ready to delete!");
            s3.deleteBucket(bucket_name);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        System.out.println("Done!");
    }
}

/** LOG 输出*/
/**
Deleting S3 bucket: cdc-dev-portal-3
- removing objects from bucket
- removing objects from bucket bucket_name=cdc-dev-portal-3 key=2017-10-17-10-21-13-06BE05379629E56D
- removing objects from bucket bucket_name=cdc-dev-portal-3 key=2017-10-17-10-22-45-FD3B18F3A376673D
- removing objects from bucket bucket_name=cdc-dev-portal-3 key=2017-10-17-10-25-01-962215C8473D5F79
- removing objects from bucket bucket_name=cdc-dev-portal-3 key=2017-10-17-10-25-42-CE92BC381B12B53F
- removing objects from bucket bucket_name=cdc-dev-portal-3 key=2017-10-17-10-26-23-E23A68E89556853A
- removing objects from bucket bucket_name=cdc-dev-portal-3 key=2017-10-17-10-26-33-C362CE0EB5C4F71A
- removing objects from bucket bucket_name=cdc-dev-portal-3 key=2017-10-17-10-27-24-C67D310DACF43D46
- removing objects from bucket bucket_name=cdc-dev-portal-3 key=2017-10-17-10-27-35-14C7F0DAF18233B0
- removing objects from bucket bucket_name=cdc-dev-portal-3 key=2017-10-17-10-28-30-FEEA9D8F0A138EE7
- removing objects from bucket bucket_name=cdc-dev-portal-3 key=2017-10-17-10-28-39-453F3D2EF79589B3
- removing objects from bucket bucket_name=cdc-dev-portal-3 key=2017-10-17-10-28-55-4EAD4FB4588BA3C6
- removing objects from bucket bucket_name=cdc-dev-portal-3 key=2017-10-17-10-29-03-6721215B202CAD8B
- removing objects from bucket bucket_name=cdc-dev-portal-3 key=2017-10-17-10-29-56-52F40160ED6CE5A8
- removing objects from bucket bucket_name=cdc-dev-portal-3 key=2017-10-17-10-30-15-4B8A956C5A1452A9
- removing objects from bucket bucket_name=cdc-dev-portal-3 key=2017-10-17-10-32-35-F4BC74AE964F05AC
- removing objects from bucket bucket_name=cdc-dev-portal-3 key=2017-10-17-10-33-02-784BC121D1BFC098
- removing objects from bucket bucket_name=cdc-dev-portal-3 key=2017-10-17-10-33-15-8A490D48D5ECD764
- removing objects from bucket bucket_name=cdc-dev-portal-3 key=9999/
- removing objects from bucket bucket_name=cdc-dev-portal-3 key=9999/666/
- removing objects from bucket bucket_name=cdc-dev-portal-3 key=9999/666/user (0)_20170919111937.xlsx222
- removing objects from bucket bucket_name=cdc-dev-portal-3 key=9999/user (0)_20170919111937.xlsx222
- removing versions from bucket
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-21-13-06BE05379629E56D versionId=3eY7P32tnn_zYZC3oiMOorOS.t6DPAPL
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-21-13-06BE05379629E56D versionId=JQ5xnMrugfeZAMRWm4fW9iOfOm24oag0
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-22-45-FD3B18F3A376673D versionId=xuaZfFY16L2U8Dx66mgZ3sl_RS_7rDXC
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-22-45-FD3B18F3A376673D versionId=jjUWx12DJB0eJBq9ejdZbh6dw61ruUa8
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-25-01-962215C8473D5F79 versionId=XY6u2WGDGjT7l1jng0uCLGCaALG5B0Bg
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-25-01-962215C8473D5F79 versionId=6SuBr3ZaH4RowiJetoFyv5zdBZe_.n8w
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-25-42-CE92BC381B12B53F versionId=DMtGPedLVWW.HclMDm5AhPGv0_DAYfxk
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-25-42-CE92BC381B12B53F versionId=SLubGxdMQVwcocVvVhd590S7mDxhrsav
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-26-23-E23A68E89556853A versionId=l0AQqbW4jfeJ6SQbbB5oVhMX5MCEdznR
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-26-23-E23A68E89556853A versionId=GDpJAmu_3PlTa8Nqhdjz3bG6dOLYt8Tu
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-26-33-C362CE0EB5C4F71A versionId=5KuCAj5kS7vU9ez9q4CoJvYQ0ZZc3W6u
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-26-33-C362CE0EB5C4F71A versionId=USFhzIZivfaYY2HIeBDaezcOR.Kisr_N
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-27-24-C67D310DACF43D46 versionId=eCgXIGloGI.b34z7cl.zRNULtsA.hBaY
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-27-24-C67D310DACF43D46 versionId=R8Hr_SJltDyYHWf8WHqL5ukCRK8vWGop
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-27-35-14C7F0DAF18233B0 versionId=AOIL93BhuE35VGSbUgJXMQHpk80Qeeav
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-27-35-14C7F0DAF18233B0 versionId=PNd1gGW2HtjWSkTg2.1p4KToPf_7cYmt
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-28-30-FEEA9D8F0A138EE7 versionId=3r1njqnNaHw_.mZEBOz5vVENCGRqJ6Df
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-28-30-FEEA9D8F0A138EE7 versionId=pbZjUMXIrPrfCbrKfMOSJ5uKTv1vEc54
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-28-39-453F3D2EF79589B3 versionId=23NnkqLaLEGbTGp.zPgBm819PxwdA03M
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-28-39-453F3D2EF79589B3 versionId=k9fmWfdSa5I26ceyLy.tweOPHRKiD4I0
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-28-55-4EAD4FB4588BA3C6 versionId=lNS5wD4bTiK5sTEHZ82Savzboqjc99mc
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-28-55-4EAD4FB4588BA3C6 versionId=x0ixI5xQVFHUMJBRDyiihAhGGCrPFzqe
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-29-03-6721215B202CAD8B versionId=FavYafi5M8LVsHf7QRjD11G5bLhaPR80
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-29-03-6721215B202CAD8B versionId=JnPd45k0QRIMuxOBeqmk3S9nvMkk9cyY
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-29-56-52F40160ED6CE5A8 versionId=ceh4XsflcMc0xzcgxOJUPV9u5TMG8Ajo
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-29-56-52F40160ED6CE5A8 versionId=HgE3XzITDIp2hsY84x_wA3ohsTr6inAw
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-30-15-4B8A956C5A1452A9 versionId=eattucNDdoboS3GOprjc.YQRCUm6.4Ew
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-30-15-4B8A956C5A1452A9 versionId=rUjAEt8B4.K3JyjOshpn1ji0YJsyrgdi
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-32-35-F4BC74AE964F05AC versionId=xHkmAy_KJJWjAVo.iUS9YMwUP0RhB4wv
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-32-35-F4BC74AE964F05AC versionId=UKoVkqdUdW6LE9wgPqWxcXKk7KnEjZqX
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-33-02-784BC121D1BFC098 versionId=XBmh9aslRg9b3l1xQS.AzLpEIr7vL1mN
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-33-02-784BC121D1BFC098 versionId=dlw7BDAs6vJNdIMusYyBBtZZCvZltXd9
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-33-15-8A490D48D5ECD764 versionId=9tfKDyAH.Z8LHKcavCJcWTzUIZJtuvo.
- removing objects from version bucket_name=cdc-dev-portal-3 key=2017-10-17-10-33-15-8A490D48D5ECD764 versionId=5Bdff7SN9SXHGi0GjWGTV9STpgitMurq
- removing objects from version bucket_name=cdc-dev-portal-3 key=9999/ versionId=1q7FMewZhg_hVOLMwmab2Crj7I9.fOXe
- removing objects from version bucket_name=cdc-dev-portal-3 key=9999/ versionId=rNaW2S2keWxMzpfD1eJOM.n4_gTouDmL
- removing objects from version bucket_name=cdc-dev-portal-3 key=9999/666/ versionId=lbqSCYIFimhko1FNHZ98xErv4o.6v7ik
- removing objects from version bucket_name=cdc-dev-portal-3 key=9999/666/ versionId=01HcS8I4KAeVrgCrZZhu_isYImvjrRWS
- removing objects from version bucket_name=cdc-dev-portal-3 key=9999/666/user (0)_20170919111937.xlsx222 versionId=G67C1qwOyBhUOmPpr9V6MeI6i6zztjph
- removing objects from version bucket_name=cdc-dev-portal-3 key=9999/666/user (0)_20170919111937.xlsx222 versionId=y_ejMOiNUXla.vXRgCE3o2w5to.KY.Fe
- removing objects from version bucket_name=cdc-dev-portal-3 key=9999/user (0)_20170919111937.xlsx versionId=zTAU4.CmhKwPrki46DkGn1QuD.G7P.xw
- removing objects from version bucket_name=cdc-dev-portal-3 key=9999/user (0)_20170919111937.xlsx versionId=_9I0LolgarnhaG62U.gxdYel7P563.Fe
- removing objects from version bucket_name=cdc-dev-portal-3 key=9999/user (0)_20170919111937.xlsx222 versionId=zqK3_ZE16lHDch0v3SgGTvePh6AI0hdZ
- removing objects from version bucket_name=cdc-dev-portal-3 key=9999/user (0)_20170919111937.xlsx222 versionId=uH9ALF0iVJJxfcLBdSvUxVVx4EPBtI.9
OK, bucket ready to delete!
Done!
*/