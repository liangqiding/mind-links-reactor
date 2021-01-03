package com.mind.links.common.response;

import com.mind.links.common.enums.LinksExceptionEnum;
import reactor.core.publisher.Mono;

/**
 * @author qiding
 */
public interface WebFluxResponse {

    /**
     * 通用请求响应
     *
     * @param response 异步序列
     * @param <T> 类型
     * @return Mono<ResponseResult<T>>
     */
    static <T> Mono<ResponseResult<T>> transform(Mono<T> response) {
        return transform(LinksExceptionEnum.OK.getCode(), LinksExceptionEnum.OK.getMsg(), response);
    }

    /**
     * 通用请求响应
     *
     * @param code 状态码
     * @param response 异步序列
     * @param <T> 类型
     * @return Mono<ResponseResult<T>>
     */
    static <T> Mono<ResponseResult<T>> transform(Integer code, Mono<T> response) {
        return transform(code,LinksExceptionEnum.getMsgByCode(code), response);
    }

    /**
     * 通用请求响应
     *
     * @param code 状态码
     * @param message 消息体
     * @param response 异步序列
     * @param <T> 类型
     * @return Mono<ResponseResult<T>>
     */
    static <T> Mono<ResponseResult<T>> transform(Integer code, String message, Mono<T> response) {
        return response.map(data -> {
            final ResponseResult<T> rw = new ResponseResult<T>();
            rw.setData(data);
            rw.setCode(code);
            rw.setMessage(message);
            return rw;
        });
    }

}
