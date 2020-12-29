package com.mind.links.common.enums;


import org.springframework.util.StringUtils;

/**
 * @author ：lqd
 * Date ：Created in 2020/11/11 0011 8:14
 * description：
 */
public enum LinksExceptionEnum {
    /**
    *  Exception CODE AND MSG
    */
    OK(20000,"业务请求成功"),
    USER_HAD(30001,"用户名已存在"),
    UNAUTHORIZED(30002,"用户未授权"),
    USER_NOT_FOUND(30003,"用户不存在"),
    REQUEST_PARAM_NOTNULL(30501, "缺少Servlet请求参数"),
    DATASOURCE_ERROR(30502, "数据库查询异常"),
    HTTP_REQUEST_METHOD_ERROR(30503, "不支持的请求类型"),
    BIND_ERROR(30505, "查询参数异常"),
    SYSTEM_ERROR(-1, "系统异常"),
    OTHER_ERROR(40000, "未知异常");

    private final Integer code;
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
        if (!StringUtils.isEmpty(code)) {
            LinksExceptionEnum[] var1 = values();
            int var2 = var1.length;
            for (LinksExceptionEnum errorEnum : var1) {
                if (errorEnum.getCode().equals(code)) {
                    return errorEnum.msg;
                }
            }
        }
        return "未知异常>>>"+code;
    }

}
