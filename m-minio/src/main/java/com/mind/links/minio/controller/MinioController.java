package com.mind.links.minio.controller;

import com.mind.links.common.response.ResponseResult;
import com.mind.links.minio.handler.MinioUtils;
import com.mind.links.logger.handler.aopLog.CustomAopHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


/**
 * description : TODO minio文件服务器对内接口
 *
 * @author : qiDing
 * date: 2020-12-21 13:42
 * @version v1.0.0
 */
@RestController
@RequestMapping("/minio")
@Api("文件服务器")
@Validated
public class MinioController {
    @Autowired
    private MinioUtils minioUtils;

    @GetMapping(value = "/create/bucket", produces = {MediaType.APPLICATION_STREAM_JSON_VALUE})
    @ApiOperation("创建存储桶")
    @CustomAopHandler(module = "minio", desc = "创建存储桶")
    public Mono<ResponseResult<String>> createBucket(@NotBlank(message = "存储桶名称不能为空") String bucketName) {
        return ResponseResult.transform(minioUtils.createBucketName(bucketName));
    }

    @PostMapping(value = "/stream/uploadObject", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "上传文件",notes = "先缓存本地，再上传至minio")
    @CustomAopHandler
    public Mono<ResponseResult<String>> uploadObject(@NotNull(message = "文件参数不能为空") @RequestPart(value = "file") Mono<FilePart> file,
                                                     @NotBlank(message = "存储桶参数不能为空") @RequestPart(value = "bucketName") String bucketName,
                                                     @NotBlank(message = "保存路径参数不能为空") @RequestPart(value = "path") String path
    ) {
        return ResponseResult.transform(minioUtils.uploadObject(file, bucketName, path));
    }

    @DeleteMapping(value = "/bucket/delete", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    @ApiOperation("删除存储桶")
    @CustomAopHandler(module = "minio", desc = "删除存储桶")
    public Mono<ResponseResult<String>> deleteBucket(@NotBlank(message = "存储桶参数不能为空") String bucketName) {
        return ResponseResult.transform(minioUtils.deleteBucket(bucketName));
    }

    @DeleteMapping(value = "/file/delete", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    @ApiOperation("删除文件")
    @CustomAopHandler(module = "minio", desc = "删除文件")
    public Mono<ResponseResult<String>> deleteObject(@NotBlank(message = "存储桶参数不能为空")  String bucketName,
                                                     @NotBlank(message = "保存路径参数不能为空") String path
    ) {
        return ResponseResult.transform(minioUtils.deleteObject(bucketName, path));
    }

    @GetMapping(value = "/images/get", produces = MediaType.IMAGE_PNG_VALUE)
    @ApiOperation("获取图片")
    @CustomAopHandler(module = "minio", desc = "获取图片")
    public Mono<byte[]> getFiles(@NotBlank(message = "保存路径参数不能为空") String filePath,
                                 @NotBlank(message = "存储桶参数不能为空") String bucketName) {
        return minioUtils.getFile(bucketName, filePath);
    }

    @GetMapping(value = "/file/get", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ApiOperation("下载文件")
    @CustomAopHandler(module = "minio", desc = "下载文件")
    public Mono<byte[]> getFile(@NotBlank(message = "保存路径参数不能为空") String filePath,
                                @NotBlank(message = "存储桶参数不能为空") String bucketName,
                                final ServerHttpResponse response
    ) {
        String filename = filePath.substring(filePath.lastIndexOf("/") + 1);
        response.getHeaders().set("Content-Disposition", "attachment;fileName=" + filename);
        return minioUtils.getFile(bucketName, filePath);
    }
}
