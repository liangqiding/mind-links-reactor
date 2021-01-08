package com.mind.links.common.enums;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author ：lqd
 * Date ：Created in 2020/11/11 0011 8:14
 * description：
 */
@ApiModel("公共异常枚举")
public enum LinksExceptionEnum {

    /**
     * Exception CODE AND MSG
     */

    OK(20000, "业务请求成功"),
    ERROR(40000, "业务请求失败"),
    USER_HAD(30001, "用户名已存在"),
    UNAUTHORIZED(30002, "用户未授权"),
    USER_NOT_FOUND(30003, "用户不存在"),
    BAD_CREDENTIALS(30004, "密码错误"),
    USER_LOCKED(30005, "用户被锁"),
    BAD_REQUEST(30400, "错误的请求"),
    REQUEST_PARAM_NOTNULL(30500, "缺少Servlet请求参数"),
    REQUEST_HEADER_NOTNULL(30501, "缺少Servlet请求头"),
    DATASOURCE_ERROR(30502, "数据库查询异常"),
    VALIDATION(30503, "参数校验异常"),
    BIND_EXCEPTION(30504, "参数校验异常"),
    NULL_POINTER(30506, "空值异常"),
    ARITHMETIC(30507, "算术异常"),
    SERVER_WEB_INPUT(30508, "缺少必要的请求参数"),
    HTTP_REQUEST_METHOD_ERROR(30509, "不支持的请求类型"),
    RECONNECTION(30000, "尝试重连"),
    SYSTEM_ERROR(-1, "系统异常"),
    OTHER_ERROR(-200, "未知异常"),
    UNDECLARED(-20000, "未声明的异常");

    @ApiModelProperty("状态码")
    private final Integer code;

    @ApiModelProperty("消息")
    private final String msg;

    LinksExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }


    public static String getMsgByCode(Integer code) {
        AtomicReference<String> msg = new AtomicReference<>("未知异常---" + code);
        Optional.ofNullable(code).flatMap(s -> Arrays.stream(values())
                .filter(e -> (s.equals(e.code)))
                .findFirst())
                .ifPresent(e -> msg.set(e.msg));
        return msg.get();
    }


}
