package com.mind.links.controller;

import com.mind.links.common.response.ResponseResult;
import com.mind.links.handler.MinioUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;


/**
 * description : TODO
 *
 * @author : qiDing
 * date: 2020-12-21 13:42
 * @version v1.0.0
 */
@RestController
@RequestMapping("/minio")
@Api("文件服务器")
public class MinioController {
    @Autowired
    private MinioUtil minioUtil;

    @GetMapping(value = "/create/bucket", produces = {MediaType.APPLICATION_STREAM_JSON_VALUE})
    @ApiOperation("创建存储桶")
    public Flux<ResponseResult<String>> createBucket(String[] bucketName) {
        return minioUtil.createBucketName(bucketName);
    }

    @GetMapping(value = "/delete/bucket", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    @ApiOperation("删除存储桶")
    public Flux<String> deleteBucket(String[] bucketName) {
        return Flux.just("key1", "key2")
                .flatMap(k -> toString(k)
                        .onErrorResume(e -> toString2(k))
                );
    }

    public Flux<String> test(String bucketName) {
        return Flux.just("key1", "key2")
                .flatMap(k -> toString(k)
                        .onErrorResume(e -> toString2(k))
                );
    }


    public Flux<String> toString(String key) {
        return Flux.just(key
        ).map(s -> {
            int i = 1 / 0;
            return "6666";
        });
    }

    public Flux<String> toString2(String key) {
        return Flux.just(key).log().map(s -> {
            return "777";
        });
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
}
