package com.mind.links.controller;

import com.mind.links.common.response.ResponseResult;
import com.mind.links.handler.MinioUtil;
import com.mind.links.logger.handler.aopLog.CustomAopHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
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
    private MinioUtil minioUtil;

    @GetMapping(value = "/create/bucket", produces = {MediaType.APPLICATION_STREAM_JSON_VALUE})
    @ApiOperation("创建存储桶")
    public Mono<ResponseResult<String>> createBucket(@NotBlank(message = "存储桶名称不能为空") String bucketName) {
        return ResponseResult.transform(minioUtil.createBucketName(bucketName));
    }

    @PostMapping(value = "/stream/uploadObject", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation("上传文件")
    @CustomAopHandler
    public Mono<ResponseResult<String>> uploadObject(@NotNull(message = "文件参数不能为空") @RequestPart(value = "file") Mono<FilePart> file,
                                                     @NotBlank(message = "存储桶参数不能为空") @RequestPart(value = "bucketName") String bucketName,
                                                     @NotBlank(message = "保存路径参数不能为空") @RequestPart(value = "path") String path
    ) {
        return ResponseResult.transform(minioUtil.uploadObject(file, bucketName, path));
    }


    @GetMapping(value = "/delete/bucket", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ApiOperation("删除存储桶")
    public Flux<String> deleteBucket(String[] bucketName) {
        return null;
    }

    /**
     * @author ：qiDing
     * @date ：Created in 2020/12/30 14:32
     * description：TODO 获取图片，可直接显示
     */
    @GetMapping(value = "/images/get", produces = MediaType.IMAGE_PNG_VALUE)
    @CustomAopHandler(module = "file", desc = "获取图片")
    public Mono<byte[]> getFiles(@NotBlank(message = "保存路径参数不能为空") String filePath,
                                 @NotBlank(message = "存储桶参数不能为空") String bucketName) {
        Mono<byte[]> file = minioUtil.getFile(bucketName, filePath);
        System.out.println("=================异步测试===================================");
        return file;
    }

    /**
     * @author ：qiDing
     * @date ：Created in 2020/12/30 14:32
     * description：TODO 获取文件,下载方式
     */
    @GetMapping(value = "/file/get", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @CustomAopHandler(module = "file", desc = "获取图片")
    public Mono<byte[]> getFile(@NotBlank(message = "保存路径参数不能为空") String filePath,
                                @NotBlank(message = "存储桶参数不能为空") String bucketName,
                                final ServerHttpResponse response
    ) {
        String filename = filePath.substring(filePath.lastIndexOf("/") + 1);
        response.getHeaders().set("Content-Disposition", "attachment;fileName=" + filename);
        return minioUtil.getFile(bucketName, filePath);
    }

}
