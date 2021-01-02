package com.mind.links.minio.handler;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author qiding
 */
public interface MinioUtil {

    /**
     * 上传至minio服务器，流程： web文件->服务器缓存->minio服务器
     *
     * @param file       文件
     * @param bucketName 存储桶
     * @param minIoPath  保存路径
     * @return 操作反馈
     */
    Mono<String> uploadObject(Mono<FilePart> file, String bucketName, String minIoPath);


    /**
     * 从本地上传至minio服务器 无存储桶判断
     *
     * @param file         文件
     * @param bucketName   存储桶
     * @param minIoPath    保存路径
     * @param saveFileName 本地的保存路径
     * @return 操作反馈
     */
    Mono<String> saveFilePart(Mono<FilePart> file, String bucketName, String saveFileName, String minIoPath);

    /**
     * 保存文件到本地并重构文件名为uuid
     *
     * @param filePart filePart
     * @return 返回保存路径
     */
    default Mono<String> saveFileCache(FilePart filePart) {
        final String uuid = UUID.randomUUID().toString().replace("-", "");
        final String fileName = uuid + filePart.filename().substring(filePart.filename().lastIndexOf("."));
        return this.saveFileCache(filePart, fileName);
    }

    /**
     * 保存文件到本地
     *
     * @param filePart filePart
     * @param fileName 文件名
     * @return 返回保存路径
     * @throws IOException io
     */
    Mono<String> saveFileCache(FilePart filePart, String fileName);

    /**
     * 存储桶不存在时,不抛异常
     *
     * @param bucketName 存储桶
     * @return true or
     */
    default Mono<Boolean> bucketExist(String bucketName) {
        return this.bucketExist(bucketName, false);
    }

    /**
     * 判断存储桶是否存在
     *
     * @param bucketName 存储桶
     * @param throwIf    是否抛异常
     * @return true or
     */
    Mono<Boolean> bucketExist(String bucketName, boolean throwIf);


    /**
     * 获取文件
     *
     * @param bucketName 存储桶
     * @param objectName 保存路径+文件名
     * @return 字节流
     */
    Mono<InputStream> getObjects(String bucketName, String objectName);

    /**
     * 创建存储桶
     *
     * @param bucketName 存储桶
     * @return 操作反馈
     */
    Mono<String> createBucketName(String bucketName);

    /**
     * 获取文件字节流
     *
     * @param bucketName 存储桶
     * @param minIoPath  保存路径+文件名
     * @return 字节流
     */
    Mono<byte[]> getFile(String bucketName, String minIoPath);

    /**
     * 删除存储桶
     *
     * @param bucketName 存储桶
     * @return true or
     */
    Mono<String> deleteBucket(String bucketName);

    /**
     * 删除文件
     *
     * @param bucketName 存储桶
     * @param minIoPath  保存路径+文件名
     * @return 操作反馈
     */
    Mono<String> deleteObject(String bucketName, String minIoPath);

}
