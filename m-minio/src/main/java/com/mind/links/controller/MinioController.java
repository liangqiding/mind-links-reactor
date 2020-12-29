package com.mind.links.controller;

import com.mind.links.common.response.ResponseResult;
import com.mind.links.handler.MinioUtil;
import com.mind.links.logger.handler.aopLog.CustomAopHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;


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
    public Flux<ResponseResult<String>> createBucket(@NotNull(message = "bucketName 参数不能为空") String[] bucketName) {
        return minioUtil.createBucketName(bucketName);
    }

    @PostMapping(value = "/stream/uploadObject", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation("创建存储桶")
    @CustomAopHandler
    public Mono<ResponseResult<String>> uploadObject(@RequestPart("file") @NotNull(message = "文件参数不能为空") Mono<FilePart> file,
                                                     @RequestPart(value = "bucketName") @NotNull(message = "存储桶参数不能为空") String bucketName,
                                                     @RequestPart(value = "path") @NotNull(message = "保存路径参数不能为空") String path
    ) {
        System.out.println("json:" + bucketName + "---" + path);
        return minioUtil.uploadObject(file, bucketName, path);
    }


    @GetMapping(value = "/delete/bucket", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ApiOperation("删除存储桶")
    public Flux<String> deleteBucket(String[] bucketName) {
        return null;
    }

    /**
     * @author ：qiDing
     * @date ：Created in 2020/12/23 08:59
     * description：TODO 浏览器测试
     */
    @ApiOperation("浏览器测试接口")
    @GetMapping(value = "/", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Object> test() {
        return Flux.interval(Duration.ofSeconds(1)).map(l -> new ResponseResult<>(new SimpleDateFormat("HH:mm:ss").format(new Date())));
    }

    @PostMapping(value = "/test", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @CustomAopHandler(checkPage = true)
    public Mono<String> test2(@RequestPart(value = "pageNumber") String pageNumber,
                              @RequestPart("pageSize") String pageSize) {
        System.out.println(pageNumber + ":::" + pageSize);
        return Mono.just("success");
    }

}
