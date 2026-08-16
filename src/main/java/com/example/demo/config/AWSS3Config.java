package com.example.demo.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class AWSS3Config {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    // Set to point at MinIO (e.g. http://minio:9000) - blank/unset falls back to real AWS S3.
    // NOTE: previously this property existed in application.properties but nothing actually
    // read it - the upload path used a *different*, separately auto-configured AmazonS3 bean
    // (from spring-cloud-starter-aws's own cloud.aws.* auto-config) that has no concept of a
    // custom endpoint at all. This bean is now the single AmazonS3 the whole app uses, and
    // spring-cloud-starter-aws's S3 auto-configuration backs off automatically once a bean of
    // this type already exists (@ConditionalOnMissingBean), so there's no duplicate-bean clash.
    @Value("${cloud.aws.s3.endpoint:}")
    private String endpoint;

    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setUseExpectContinue(false);

        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withClientConfiguration(clientConfig);

        if (StringUtils.hasText(endpoint)) {
            builder.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                    .withPathStyleAccessEnabled(true)
                    .disableChunkedEncoding();  // 추가
        } else {
            builder.withRegion(region);
        }

        return builder.build();
    }

}
