package com.mind.links.handler;

import com.mind.links.common.exception.LinksException;
import com.mind.links.common.response.ResponseResult;
import io.minio.*;
import io.minio.errors.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * description : TODO
 *
 * @author : qiDing
 * date: 2020-12-07 13:06
 * @version v1.0.0
 */
@Component
@Slf4j
public class MinioUtil {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;


    /**
     * 上传文件
     */
    public void uploadObject(String locationPathName, String minIoPathName) {
        Map<String, String> headers = new HashMap<>(10);
        if (locationPathName.contains(".png")) {
            headers.put("Content-Type", "image/png");
        }
        if (locationPathName.contains(".jpg")) {
            headers.put("Content-Type", "image/jpeg");
        }
        if (locationPathName.contains(".gif")) {
            headers.put("Content-Type", "image/gif");
        }
        try {
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(minIoPathName)
                            .filename(locationPathName)
                            .headers(headers)
                            .build());
            log.info("===文件上传成功！===");
        } catch (ErrorResponseException | InvalidKeyException | InsufficientDataException | InternalException | InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException | XmlParserException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过InputStream上传对象
     */
    @SneakyThrows
    public void putObject(MultipartFile multipartFile, String filename) {

        //创建头部信息
        Map<String, String> headers = new HashMap<>(10);
        //添加自定义内容类型
        headers.put("Content-Type", "application/octet-stream");
        //添加存储类
        headers.put("X-Amz-Storage-Class", "REDUCED_REDUNDANCY");
        InputStream stream = multipartFile.getInputStream();
        minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(filename).stream(
                stream, stream.available(), -1)
                .contentType(multipartFile.getContentType())
                .headers(headers)
                .build());
    }

    /**
     * 删除文件
     */
    public void deleteObject(String minIoPathName) {
        try {
            minioClient.removeObject(RemoveObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .object(minIoPathName)
                    .build()
            );
            log.info("===文件删除成功！===");
        } catch (ErrorResponseException | InvalidKeyException | InsufficientDataException | InternalException | InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException | XmlParserException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查存储桶是否存在
     */
    @SneakyThrows
    public boolean bucketExists() {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(this.bucketName).build());
    }

    /**
     * 以流的形式获取一个文件对象
     */
    @SneakyThrows
    public InputStream getObject(String objectName) {
        return bucketExists() ? minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build()) : null;
    }

    /**
     * @author ：qiDing
     * @date ：Created in 2020/12/23 09:27
     * description：TODO 创建存储桶
     */
    public Flux<ResponseResult<String>> createBucketName(String[] bucketNames) {
        return Flux.fromIterable(Arrays.asList(bucketNames.clone())).log().map(b -> {
            try {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(b).build());
            } catch (Exception e) {
                throw new LinksException(20406, "创建异常");
            }
            return new ResponseResult<>("创建成功");
        }).onErrorResume(throwable -> {
            throwable.printStackTrace();
            return Mono.just(new ResponseResult<>("创建失败"));
        });
    }

}
