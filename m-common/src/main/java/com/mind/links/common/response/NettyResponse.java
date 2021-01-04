package com.mind.links.common.response;


import com.alibaba.fastjson.JSONObject;


/**
 * @author qiding
 */
@FunctionalInterface
public interface NettyResponse<T> {

    /**
     * 启用函数式
     *
     * @param t 响应体内容
     * @return 响应体内容
     */
    T apply(T t);

    /**
     * 获取字节数组
     *
     * @param code    状态码
     * @param message 消息
     * @param data    内容
     * @return b
     */
    default byte[] getBytes(Integer code, String message, T data) {
        return this.toJsonString(code, message, data).getBytes();
    }


    /**
     * 转换为JSONString
     *
     * @param code    状态码
     * @param message 消息
     * @param data    内容
     * @return jsonString
     */
    default String toJsonString(Integer code, String message, T data) {
        JSONObject response = new JSONObject();
        response.put("code", code);
        response.put("message", message);
        response.put("data", data);
        return response.toJSONString();
    }

}
