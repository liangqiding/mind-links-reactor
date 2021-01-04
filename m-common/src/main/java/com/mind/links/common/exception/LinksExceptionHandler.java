package com.mind.links.common.exception;

import com.mind.links.common.response.ResponseResult;
import com.mind.links.common.enums.LinksExceptionEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import javax.validation.ValidationException;
import java.util.Optional;

/**
 * @author qidingliang
 * <p>
 * 公共异常处理类
 */
@Slf4j
public class LinksExceptionHandler {

    @ApiModelProperty("错误码")
    public static final Integer ERROR = 40000;

    public static Mono<ResponseResult<String>> errorHandler(Throwable t) {
        return Mono.just(errorHandler((Exception) t));
    }

    public static void throwLinksException(Throwable t) {
        throwLinksException(t, ERROR, LinksExceptionEnum.getMsgByCode(ERROR));
    }

    public static void throwLinksException(Throwable t, Integer code) {
        throwLinksException(t, code, LinksExceptionEnum.getMsgByCode(code));
    }

    public static void throwLinksException(Throwable t, Integer code, String message) {
        throw new LinksException(code, message);
    }

    public static ResponseResult<String> errorHandler(Exception ex) {
        if (ex instanceof LinksException) {
            return new ResponseResult<>(((LinksException) ex).getCode(), ex.getMessage());
        } else if (ex instanceof DataAccessException) {
            return new ResponseResult<>(30502);
        } else if (ex instanceof ValidationException) {
            return validExceptionHandler((ValidationException) ex);
        } else if (ex instanceof BindException) {
            return validExceptionHandler((BindException) ex);
        } else if (ex instanceof NullPointerException) {
            return new ResponseResult<>(30506);
        } else if (ex instanceof ArithmeticException) {
            return new ResponseResult<>(30507);
        } else if (ex instanceof ServerWebInputException) {
            return new ResponseResult<>(30508);
        }
        return new ResponseResult<>(40000);
    }

    public static ResponseResult<String> validExceptionHandler(BindException ex) {
        StringBuilder stringBuffer = new StringBuilder();
        Optional.of(ex.getBindingResult())
                .filter(Errors::hasErrors)
                .map(Errors::getFieldError)
                .map(s -> stringBuffer.append(s.getDefaultMessage()).append(",")
                );
        log.error(stringBuffer.toString());
        return new ResponseResult<>(20505, stringBuffer.toString());
    }

    public static ResponseResult<String> validExceptionHandler(ValidationException ex) {
        String message = ex.getMessage();
        String[] split = message.split(":");
        log.error(split[split.length - 1]);
        return new ResponseResult<>(20505, split[split.length - 1]);
    }
}
