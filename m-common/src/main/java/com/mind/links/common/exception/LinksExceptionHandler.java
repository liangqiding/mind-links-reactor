package com.mind.links.common.exception;

import com.mind.links.common.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import reactor.core.publisher.Mono;
import javax.validation.ValidationException;

/**
 * @author qidingliang
 * <p>
 * 公共异常处理类
 */
@Slf4j
public class LinksExceptionHandler {

    public static Mono<ResponseResult<String>> errorHandler(Throwable t) {
        return Mono.just(errorHandler((Exception) t));
    }

    public static ResponseResult<String> errorHandler(Exception ex) {
        if (ex instanceof LinksException) {
            return new ResponseResult<>(((LinksException) ex).getCode(), ex.getMessage());
//        } else if (ex instanceof MissingServletRequestParameterException) {
//            return new ResponseResult<>(20501);
        } else if (ex instanceof DataAccessException) {
            return new ResponseResult<>(20502);
        } else if (ex instanceof ValidationException) {
            return validExceptionHandler((ValidationException) ex);
        } else if (ex instanceof BindException) {
            return validExceptionHandler((BindException) ex);
//        } else if (ex instanceof HttpRequestMethodNotSupportedException) {
//            return new ResponseResult<>(20503);
        } else if (ex instanceof NullPointerException) {
            return new ResponseResult<>(20506, "空的值");
        } else if (ex instanceof ArithmeticException) {
            return new ResponseResult<>(20506, "算术异常");
        }
        return new ResponseResult<>(40000);
    }

    public static ResponseResult<String> validExceptionHandler(BindException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        StringBuilder stringBuffer = new StringBuilder();
        if (bindingResult.hasErrors()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                stringBuffer.append(fieldError.getDefaultMessage()).append(",");
            }
        }
        log.error(stringBuffer.toString());
        return new ResponseResult<>(20505, stringBuffer.toString());
    }

    public static ResponseResult<String> validExceptionHandler(ValidationException ex) {
        String message = ex.getMessage();
        String[] split = message.split(":");
        log.error(split[split.length-1]);
        return new ResponseResult<>(20505, split[split.length-1]);
    }
}
