//package com.links.common.exception;
//
//import com.links.common.utils.ResponseResult;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.context.config.annotation.RefreshScope;
//import org.springframework.dao.DataAccessException;
//import org.springframework.validation.BindException;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.FieldError;
//import org.springframework.web.HttpRequestMethodNotSupportedException;
//import org.springframework.web.bind.MissingServletRequestParameterException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
///**
// * @Author: QiDing
// * @DateTime: 2020/11/26 0026 8:10
// * @Description: TODO
// */
//@RestControllerAdvice
//@RefreshScope
//public class CustomExceptionHandler {
//    private final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);
//
//    @Value("${exception.printStackTrace:}")
//    private Boolean printStackTrace;
//
//    @ExceptionHandler(value = Exception.class)
//    public ResponseResult<String> errorHandler(Exception ex) {
//        if (printStackTrace){
//            logger.error("********************************************执行异常打印*****************************************************");
//            ex.printStackTrace();
//        }
//        logger.error("************************"+ex.getMessage()+"************************");
//        //判断异常的类型,返回不一样的返回值
//        if (ex instanceof MissingServletRequestParameterException) {
//            return new ResponseResult<>(20501);
//        } else if (ex instanceof DataAccessException) {
//            return new ResponseResult<>(20502);
//        }else if (ex instanceof BindException){
//            return this.validExceptionHandler((BindException)ex);
//        }else if (ex instanceof HttpRequestMethodNotSupportedException){
//            return new ResponseResult<>(20503);
//        }else if (ex instanceof NullPointerException){
//            return new ResponseResult<>(20506, "空的值");
//        }
//        return new ResponseResult<>(20500);
//    }
//
//    public ResponseResult<String> validExceptionHandler(BindException ex) {
//        BindingResult bindingResult = ex.getBindingResult();
//        StringBuilder stringBuffer = new StringBuilder();
//        if(bindingResult.hasErrors()){
//            for (FieldError fieldError : bindingResult.getFieldErrors()) {
//                stringBuffer.append(fieldError.getDefaultMessage()).append(",");
//            }
//        }
//        logger.error(stringBuffer.toString());
//        return new ResponseResult<>(20505,stringBuffer.toString());
//    }
//    @ExceptionHandler(value = LinksException.class)
//    public ResponseResult<String> myErrorHandler(LinksException ex) {
//        return new ResponseResult<>(ex.getCode(), ex.getMessage());
//    }
//}
