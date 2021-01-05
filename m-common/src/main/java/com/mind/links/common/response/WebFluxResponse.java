package com.mind.links.common.response;

import com.mind.links.common.enums.LinksExceptionEnum;
import reactor.core.publisher.Mono;

/**
 * webFlux 下的通用 response
 *
 * @author qiding
 */
@FunctionalInterface
public interface WebFluxResponse<T> {

    /**
     * 接受一个响应的值
     *
     * @param t response 响应的值
     * @return Mono<ResponseResult < T>>
     */
    T apply(T t);

    /**
     * 通用请求响应
     *
     * @param response 异步序列
     * @return Mono<ResponseResult < T>>
     */
    default Mono<ResponseResult<T>> transform(Mono<T> response) {
        return transform(LinksExceptionEnum.OK.getCode(), LinksExceptionEnum.OK.getMsg(), response);
    }

    /**
     * 通用请求响应
     *
     * @param code     状态码
     * @param response 异步序列
     * @return Mono<ResponseResult < T>>
     */
    default Mono<ResponseResult<T>> transform(Integer code, Mono<T> response) {
        return transform(code, LinksExceptionEnum.getMsgByCode(code), response);
    }

    /**
     * 通用请求响应
     *
     * @param code     状态码
     * @param message  消息体
     * @param response 异步序列
     * @return Mono<ResponseResult < T>>
     */
    default Mono<ResponseResult<T>> transform(Integer code, String message, Mono<T> response) {
        return response.map(data -> new ResponseResult<T>().setData(data).setCode(code).setMessage(message));
    }

}
