package com.mind.links.common.response;


import com.alibaba.fastjson.JSONObject;
import com.mind.links.common.enums.LinksExceptionEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import reactor.core.publisher.Mono;
import java.io.Serializable;

/**
 * @author qiding
 */
@Data
@ApiModel("公共响应体")
public class ResponseResult<T> implements WebFluxResponse, NettyResponse, Serializable {

    private static final long serialVersionUID = -1L;

    @ApiModelProperty("状态码")
    private Integer code = LinksExceptionEnum.OK.getCode();

    @ApiModelProperty("操作反馈")
    private String message=LinksExceptionEnum.OK.getMsg();

    @ApiModelProperty("响应内容")
    private T data;

    public ResponseResult() {
        super();
    }

    public ResponseResult(Integer code) {
        super();
        this.code = code;
        this.message = LinksExceptionEnum.getMsgByCode(code);
    }

    public ResponseResult(T data) {
        super();
        this.data = data;
    }

    public ResponseResult(Integer code, String message) {
        super();
        this.code = code;
        this.message = message;
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
        return transform(LinksExceptionEnum.OK.getCode(), LinksExceptionEnum.OK.getMsg(), response);
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

    @Override
    public byte[] getBytes() {
        return this.toJsonString().getBytes();
    }

    @Override
    public String toJsonString() {
        JSONObject response = new JSONObject();
        response.put("code", this.code);
        response.put("message", this.message);
        response.put("data", this.data);
        return response.toJSONString();
    }
}
