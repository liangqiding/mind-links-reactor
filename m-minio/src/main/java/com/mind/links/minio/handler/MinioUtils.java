package com.mind.links.minio.handler;

import com.mind.links.common.enums.LinksContentTypeEnum;
import com.mind.links.common.exception.LinksException;
import com.mind.links.common.utils.LinksFileManage;
import io.minio.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
/**
 * description : TODO
 *
 * @author : qiDing
 * date: 2020-12-07 13:06
 * @version v1.0.0
 */
@Component
@Slf4j
public class MinioUtils implements MinioUtil {
    @Autowired
    private MinioClient minioClient;

    @Resource(description = "myScheduler")
    private Scheduler myScheduler;


    @Override
    public Mono<String> uploadObject(Mono<FilePart> file, String bucketName, String minIoPathName) {
        AtomicReference<String> saveFileNames = new AtomicReference<>();
        return Mono.just(bucketName)
                .flatMap(b -> this.bucketExist(b, true))
                .flatMap(aBoolean -> file.flatMap(this::saveFileCache))
                .doOnNext(saveFileNames::set)
                .flatMap(saveFileName -> this.saveFilePart(file, bucketName, saveFileName, minIoPathName))
                .doFinally(signalType -> LinksFileManage.deleteDirAndFileAll(saveFileNames.get().substring(0, saveFileNames.get().lastIndexOf("/") + 1)));
    }

    @Override
    public Mono<String> saveFileCache(FilePart filePart, String filename){
        final String uuid = UUID.randomUUID().toString().replace("-", "");
        final String path = "cacheFile/";
        final File volatilize = new File(path + uuid + "/");
        final String savePath = path + uuid + "/" + filename;
        final File saveFile = new File(savePath);
        log.info("===filename=" + filename);
        return Mono.fromCallable(() -> {
            LinksFileManage.fileMkdirs(volatilize);
            filePart.transferTo(saveFile);
            return savePath;
        }).subscribeOn(myScheduler);
    }

    @Override
    public Mono<String> saveFilePart(Mono<FilePart> file, String bucketName, String saveFileName, String minIoPathName) {
        final Map<String, String> headers = new HashMap<>(10);
        return file.flatMap(filePart -> {
            LinksContentTypeEnum.setHeaders(filePart.filename(), headers);
            return Mono
                    .fromCallable(() -> {
                        String minioPathName = LinksFileManage.checkPathAndRepair(minIoPathName) + LinksFileManage.getFileName(saveFileName);
                        minioClient.uploadObject(
                                UploadObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(minioPathName)
                                        .filename(saveFileName)
                                        .headers(headers)
                                        .build());
                        return minioPathName;
                    })
                    .subscribeOn(myScheduler);
        });
    }

    @SneakyThrows
    @Override
    public Mono<Boolean> bucketExist(String bucketName, boolean throwIf) {
        return Mono.just(false).map(b -> {
            boolean exist = false;
            try {
                exist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            } catch (Exception e) {
                e.printStackTrace();
                if (throwIf) {
                    throw new LinksException(30406, "存储桶不存在");
                }
            }
            if (throwIf && !exist) {
                throw new LinksException(30406, "存储桶不存在");
            }
            return exist;
        });
    }

    @Override
    @SneakyThrows
    public Mono<InputStream> getObjects(String bucketName, String objectName) {
        return Mono.fromCallable(() -> (InputStream) minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build()))
                .subscribeOn(myScheduler);
    }


    @Override
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

    @Override
    public Mono<byte[]> getFile(String bucketName, String imagesPath) {
        return Mono.just(bucketName)
                .flatMap(o -> this.getObjects(bucketName, imagesPath))
                .map(bytes -> {
                    byte[] imageContent = new byte[0];
                    try {
                        imageContent = LinksFileManage.toByteArray(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("***minio 没有找到文件>>>" + imagesPath);
                        throw new LinksException(30607, "没有该文件");
                    }
                    return imageContent;
                });
    }

    @Override
    public Mono<String> deleteBucket(String bucketName) {
        return Mono.fromCallable(() -> {
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
            return "删除成功！";
        }).subscribeOn(myScheduler);
    }

    @Override
    public Mono<String> deleteObject(String bucketName, String minIoPathName) {
        return Mono.fromCallable(() -> {
            minioClient.removeObject(RemoveObjectArgs
                    .builder()
                    .bucket("bucketName")
                    .object(minIoPathName)
                    .build()
            );
            log.info("===文件删除成功！===");
            return "文件删除成功！";
        }).subscribeOn(myScheduler);
    }

}
