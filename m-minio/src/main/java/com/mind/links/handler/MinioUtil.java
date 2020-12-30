package com.mind.links.handler;

import com.mind.links.common.enums.LinksContentTypeEnum;
import com.mind.links.common.exception.LinksException;
import com.mind.links.common.exception.LinksExceptionHandler;
import com.mind.links.common.response.ResponseResult;
import com.mind.links.common.utils.FileManage;
import io.minio.*;
import io.minio.errors.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


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

    @Resource(description = "myScheduler")
    private Scheduler myScheduler;


    private final String PATH_TO = "cacheFile/";

    /**
     * @author ：qiDing
     * @date ：Created in 2020/12/30 13:15
     * description：TODO 上传文件
     */
    public Mono<String> uploadObject(Mono<FilePart> file, String bucketName, String minIoPathName) {
        return Mono.just(bucketName)
                .flatMap(filePart -> this.bucketExist(bucketName, true))
                .flatMap(filePart -> this.saveFilePart(file, bucketName, minIoPathName));
    }

    public Mono<String> saveFilePart(Mono<FilePart> file, String bucketName, String minIoPathName) {
        Map<String, String> headers = new HashMap<>(10);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return file.flatMap(filePart -> {
            LinksContentTypeEnum.setHeaders(filePart.filename(), headers);
            File volatilize = new File(PATH_TO + uuid + "/");
            File savePath = new File(PATH_TO + uuid + "/" + filePart.filename());
            log.info("===filename=" + filePart.filename());
            FileManage.fileMkdirs(volatilize);
            filePart.transferTo(savePath);
            return Mono.fromCallable(() -> {
                minioClient.uploadObject(
                        UploadObjectArgs.builder()
                                .bucket(bucketName)
                                .object(FileManage.checkPathAndRepair(minIoPathName) + filePart.filename())
                                .filename(PATH_TO + uuid + "/" + filePart.filename())
                                .headers(headers)
                                .build());
                return bucketName + ":上传成功";
            }).subscribeOn(myScheduler);
        }).doFinally(signalType -> FileManage.deleteDirAndFileAll(PATH_TO + uuid));
    }

    /**
     * 通过InputStream上传对象
     */
    @SneakyThrows
    public Mono<ResponseResult<String>> putObject(MultipartFile multipartFile, String bucketName, String path) {
        return Mono.fromCallable(() -> {
            Map<String, String> headers = new HashMap<>(10);
            //添加自定义内容类型
            headers.put("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE);
            //添加存储类
            headers.put("X-Amz-Storage-Class", "REDUCED_REDUNDANCY");
            InputStream stream = multipartFile.getInputStream();
            minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(FileManage.checkPathAndRepair(path) + multipartFile.getOriginalFilename()).stream(
                    stream, stream.available(), -1)
                    .contentType(multipartFile.getContentType())
                    .headers(headers)
                    .build());
            return new ResponseResult<>(bucketName + ":上传成功");
        })
                .log()
                .subscribeOn(myScheduler)
                .onErrorResume(LinksExceptionHandler::errorHandler);
    }

    /**
     * 删除文件
     */
    public void deleteObject(String minIoPathName) {
        try {
            minioClient.removeObject(RemoveObjectArgs
                    .builder()
                    .bucket("bucketName")
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

    public Mono<Boolean> bucketExist(String bucketName) {
        return this.bucketExist(bucketName, false);
    }

    @SneakyThrows
    public Mono<Boolean> bucketExist(String bucketName, boolean throwIf) {
        return Mono.just(false).map(b -> {
            try {
                return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            } catch (Exception e) {
                e.printStackTrace();
                if (throwIf) {
                    throw new LinksException(30406, "存储桶不存在");
                }
            }
            return false;
        });
    }

    /**
     * 以流的形式获取一个文件对象
     */
    @SneakyThrows
    public Mono<InputStream> getObjects(String bucketName, String objectName) {
        return Mono.fromCallable(() -> (InputStream) minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build()))
                .subscribeOn(myScheduler);
    }


    /**
     * @author ：qiDing
     * @date ：Created in 2020/12/23 09:27
     * description：TODO 创建存储桶
     */
    public Mono<String> createBucketName(String bucketName) {
        return Mono.just(bucketName)
                .flatMap(this::bucketExist)
                .map(b -> Optional.of(b)
                        .filter(exist -> !exist)
                        .map(aBoolean -> {
                            try {
                                minioClient.makeBucket(MakeBucketArgs
                                        .builder()
                                        .bucket(bucketName)
                                        .build());
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw new LinksException(30506, "存储桶：" + bucketName + "-创建失败");
                            }
                            return true;
                        })
                        .orElseThrow(() -> new LinksException(30406, "存储桶:" + bucketName + "-创建失败,存储桶已存在")) ? "存储桶:" + bucketName + "-创建成功" : "存储桶:" + bucketName + "-创建失败");
    }

    /**
     * @author ：qiDing
     * @date ：Created in 2020/12/30 13:49
     * description：TODO 获取文件字节流
     */
    public Mono<byte[]> getFile(String bucketName, String imagesPath) {
        return Mono.just(bucketName)
                .flatMap(o -> this.getObjects(bucketName, imagesPath))
                .map(bytes -> {
                    byte[] imageContent = new byte[0];
                    try {
                        imageContent = FileManage.toByteArray(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("***minio 没有找到文件>>>" + imagesPath);
                        throw new LinksException(30607, "没有该文件");
                    }
                    return imageContent;
                });
    }
}
