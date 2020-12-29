package com.mind.links.handler;

import com.mind.links.common.enums.LinksContentTypeEnum;
import com.mind.links.common.exception.LinksExceptionHandler;
import com.mind.links.common.response.ResponseResult;
import com.mind.links.common.utils.FileManage;
import com.sun.org.apache.bcel.internal.generic.FMUL;
import io.minio.*;
import io.minio.errors.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
    Scheduler myScheduler;


    private final String pathTo = "cacheFile/";

    /**
     * 上传文件
     */
    public Mono<ResponseResult<String>> uploadObject(Mono<FilePart> file, String bucketName, String minIoPathName) {
        Map<String, String> headers = new HashMap<>(10);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return file
                .filter(filePart -> this.bucketExistsAndCreate(bucketName))
                .flatMap(filePart -> {
                    LinksContentTypeEnum.setHeaders(filePart.filename(), headers);
                    File volatilize = new File(pathTo + uuid + "/");
                    File savePath = new File(pathTo + uuid + "/" + filePart.filename());
                    log.info("===filename=" + filePart.filename());
                    try {
                        this.fileMkdirs(volatilize);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    filePart.transferTo(savePath);
                    return Mono.fromCallable(() -> {
                        minioClient.uploadObject(
                                UploadObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(FileManage.checkPathAndRepair(minIoPathName) + filePart.filename())
                                        .filename(pathTo + uuid + "/" + filePart.filename())
                                        .headers(headers)
                                        .build());
                        return new ResponseResult<>(bucketName + ":上传成功");
                    }).subscribeOn(myScheduler);
                })
                .doFinally(signalType -> FileManage.deleteDirAndFileAll(pathTo + uuid));
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
    @SneakyThrows
    public boolean bucketExists(String bucketName) {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

    /**
     * 检查存储桶是否存在 不存在则创建
     */

//    @SneakyThrows
//    public <T> Mono<T> bucketExistsAndCreate(T m, String bucketName) {
//        return Mono.just(m)
//                .filter(t -> !bucketExists(bucketName))
//                .map(t -> {
//                    String[] a = {bucketName};
//                    createBucketName(a);
//                    return t;
//                });
//    }
    @SneakyThrows
    public boolean bucketExistsAndCreate(String bucketName) {
        if (!this.bucketExists(bucketName)) {
            minioClient.makeBucket(MakeBucketArgs
                    .builder()
                    .bucket(bucketName)
                    .build());
        }
        return true;
    }

    /**
     * 以流的形式获取一个文件对象
     */
    @SneakyThrows
    public InputStream getObject(String objectName) {
        return bucketExists("bucketName") ? minioClient.getObject(GetObjectArgs.builder().bucket("bucketName").object(objectName).build()) : null;
    }

    /**
     * @author ：qiDing
     * @date ：Created in 2020/12/23 09:27
     * description：TODO 批量创建存储桶
     */
    public Flux<ResponseResult<String>> createBucketName(String[] bucketNames) {
        return Flux.fromIterable(Arrays.asList(bucketNames.clone()))
                .log()
                .map(b -> {
                    try {
                        if (!this.bucketExists(b)) {
                            minioClient.makeBucket(MakeBucketArgs
                                    .builder()
                                    .bucket(b)
                                    .build());
                        } else {
                            return new ResponseResult<>(b + ":创建失败,存储已存在");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new ResponseResult<>(b + ":创建失败");
                    }
                    return new ResponseResult<>(b + ":创建成功");
                }).delayElements(Duration.ofSeconds(1))
                .onErrorResume(LinksExceptionHandler::errorHandler);
    }


    /**
     * @author 梁其定
     * Description //TODO 创建目录
     * @date 9:17 2020/4/14 0014
     **/
    public void fileMkdirs(File file) throws IOException {
        try {
            boolean b = file.setWritable(true, false);
            if (!file.getParentFile().exists()) {
                //上级目录不存在，创建上级目录
                boolean mkdirs = file.getParentFile().mkdirs();
                log.info("==============执行创建文件夹" + file.getPath() + "赋予读写权限" + b);
            }
            boolean mkdirs = file.mkdirs();
            log.info("==============执行创建文件夹" + file.getPath() + "赋予读写权限" + b);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("*********************文件夹创建失败！！********************************");
            throw new IOException("*********************文件夹创建失败！！********************************");
        }
    }
}
