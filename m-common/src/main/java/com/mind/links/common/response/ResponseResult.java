package com.mind.links.common.response;


import com.mind.links.common.enums.LinksExceptionEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author qiding
 */
@Data
@ApiModel("公共响应体")
public class ResponseResult<T> implements Serializable {

    private static final long serialVersionUID = -1L;

    @ApiModelProperty("成功码")
    public static final Integer OK = 20000;

    @ApiModelProperty("成功消息")
    public static final String OK_MESSAGE = "操作成功";

    @ApiModelProperty("错误码")
    public static final Integer ERROR = -1;

    @ApiModelProperty("错误消息")
    public static final String ERR_MESSAGE = "操作失败";

    private Integer code=OK;

    private String message;

    private T data;

    public ResponseResult() {
        super();
    }

    public ResponseResult(Integer code) {
        super();
        this.code = code;
    }
    public ResponseResult(T data) {
        super();
        this.data = data;
        this.message = LinksExceptionEnum.getMsgByCode(code);
    }
    public ResponseResult(Integer code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public ResponseResult(Integer code, Throwable throwable) {
        super();
        this.code = code;
        this.message = throwable.getMessage();
    }

    public ResponseResult(Integer code, T data) {
        super();
        this.code = code;
        this.data = data;
        this.message = LinksExceptionEnum.getMsgByCode(code);
    }

    public ResponseResult(Integer code, String message, T data) {
        super();
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void test() {

     }
}
