package links.common.enums;


import org.springframework.util.StringUtils;

/**
 * @author ：lqd
 * @Date ：Created in 2020/11/11 0011 8:14
 * description：
 * @modified By：
 * @version: $
 */
public enum MyExceptionEnum{
    /**
    *  Exception CODE AND MSG
    */
    OK(20000,"业务请求成功"),
    USER_HAD(20002,"用户名已存在"),
    UNAUTHORIZED(20003,"用户未授权"),
    USER_NOT_ACCEPTABLE(20004,"用户名校验失败"),
    USER_HAS_SENSITIVE(20005,"用户名包含敏感词汇"),
    USER_HAS_SPECIAL(20006,"用户名包含特殊词汇"),
    USER_NOT_FOUND(20007,"用户不存在"),
    REQUEST_PARAM_NOTNULL(20501, "缺少Servlet请求参数"),
    DATASOURCE_ERROR(20502, "数据库查询异常"),
    HttpRequestMethodNotSupportedException(20503, "不支持的请求类型"),
    BindException(20505, "查询参数异常"),
    groupLvException(20506, "不存在的组或组等级错误"),
    SYSTEM_ERROR(-1, "系统异常"),
    OTHER_ERROR(20500, "未知异常");

    private final Integer code;
    private final String msg;

    MyExceptionEnum(Integer code, String msg) {
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
            MyExceptionEnum[] var1 = values();
            int var2 = var1.length;
            for (MyExceptionEnum errorEnum : var1) {
                if (errorEnum.getCode().equals(code)) {
                    return errorEnum.msg;
                }
            }
        }
        return "未知异常-"+code;
    }


}
