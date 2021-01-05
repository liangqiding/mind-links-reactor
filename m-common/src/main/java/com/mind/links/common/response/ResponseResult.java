package com.mind.links.common.response;


import com.mind.links.common.enums.LinksExceptionEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import reactor.core.publisher.Mono;
import java.io.Serializable;

/**
 * @author qiding
 */
@Data
@ApiModel("公共响应体")
@Accessors(chain = true)
public class ResponseResult<T> implements Serializable {

    private static final long serialVersionUID = -1L;

    @ApiModelProperty("状态码")
    private Integer code = LinksExceptionEnum.OK.getCode();

    @ApiModelProperty("操作反馈")
    private String message = LinksExceptionEnum.OK.getMsg();

    @ApiModelProperty("响应内容")
    private T data;

    /**
     * webFlux 下的通用返回
     */
    public static <T> Mono<ResponseResult<T>> transform(Mono<T> response) {
        return ((WebFluxResponse<T>) r -> r).transform(response);
    }

    public static <T> Mono<ResponseResult<T>> transform(Integer code, Mono<T> response) {
        return ((WebFluxResponse<T>) r -> r).transform(code, response);
    }

    public static <T> Mono<ResponseResult<T>> transform(Integer code, String message, Mono<T> response) {
        return ((WebFluxResponse<T>) r -> r).transform(code, message, response);
    }

    /**
     * netty 服务（临时）通用返回
     */

    public byte[] toBytes() {
        return ((NettyResponse<T>) o -> o).getBytes(this.code, this.message, this.data);
    }

    public String toJsonString() {
        return ((NettyResponse<T>) o -> o).toJsonString(this.code, this.message, this.data);
    }

    /**
     * http默认构造
     */
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
}
