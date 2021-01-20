package com.mind.links.minio.config;

import io.minio.MinioClient;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * description : TODO
 *
 * @author : qiDing
 * date: 2020-12-07 11:46
 * @version v1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    @ApiModelProperty("endPoint是一个URL，域名，IPv4或者IPv6地址")
    private String endpoint;

    @ApiModelProperty("accessKey类似于用户ID，用于唯一标识你的账户")
    private String accessKey;

    @ApiModelProperty("secretKey是你账户的密码")
    private String secretKey;

    @ApiModelProperty("如果是true，则用的是https而不是http,默认值是true")
    private Boolean secure;

    @ApiModelProperty("默认存储桶")
    private String bucketName;

    @ApiModelProperty("配置目录")
    private String configDir;

    @Bean
    public MinioClient getMinioClient() throws Exception {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
    
}
