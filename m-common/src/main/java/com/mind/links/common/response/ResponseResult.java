package com.mind.links.common.response;


import com.mind.links.common.enums.LinksExceptionEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

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
    public static final Integer ERROR = 40000;

    @ApiModelProperty("错误消息")
    public static final String ERR_MESSAGE = "请求失败";

    @ApiModelProperty("状态码")
    private Integer code = OK;

    @ApiModelProperty("操作反馈")
    private String message;

    @ApiModelProperty("内容")
    private T data;

    public ResponseResult() {
        super();
        this.code = ERROR;
        this.message = LinksExceptionEnum.getMsgByCode(code);
    }

    public ResponseResult(Integer code) {
        super();
        this.code = code;
        this.message = LinksExceptionEnum.getMsgByCode(code);
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


    /**
     * webFlux 下的通用返回
     */
    public static <T> Mono<ResponseResult<T>> transform(Mono<T> response) {
        return transform(OK, LinksExceptionEnum.getMsgByCode(OK), response);
    }

    public static <T> Mono<ResponseResult<T>> transform(Integer code, Mono<T> response) {
        return transform(code, LinksExceptionEnum.getMsgByCode(code), response);
    }

    public static <T> Mono<ResponseResult<T>> transform(Integer code, String message, Mono<T> response) {
        return response.map(data -> {
            final ResponseResult<T> rw = new ResponseResult<T>();
            rw.setData(data);
            rw.setCode(code);
            rw.setMessage(message);
            return rw;
        });
    }
}
