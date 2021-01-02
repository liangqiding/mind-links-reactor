package com.mind.links.minio.config;


import com.mind.links.common.exception.LinksException;
import com.mind.links.common.exception.LinksExceptionHandler;
import com.mind.links.common.response.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

/**
 * @author: QiDing
 * DateTime: 2020/7/26 0026 8:10
 * Description: TODO
 */
@RestControllerAdvice
@RefreshScope
public class CustomExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @Value("${exception.printStackTrace:}")
    private Boolean printStackTrace;

    @ExceptionHandler(value = Exception.class)
    public ResponseResult<String> errorHandler(Exception ex) {
        Optional.ofNullable(printStackTrace).ifPresent(b -> ex.printStackTrace());
        logger.error("************************" + ex.getMessage() + "************************");
        return LinksExceptionHandler.errorHandler(ex);
    }
    @ExceptionHandler(value = LinksException.class)
    public ResponseResult<String> linksException(Exception ex) {
        logger.error("***自定义异常：" + ex.getMessage() + "**************************************");
        return LinksExceptionHandler.errorHandler(ex);
    }
}
