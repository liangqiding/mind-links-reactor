package com.mind.links.common.response;


public interface NettyResponse {

    /**
     * 获取字节数组
     *
     * @return b
     */
    default byte[] getBytes(){
        return this.toJsonString().getBytes();
    };

    /**
     * 转换为json string
     *
     * @return jsonString
     */
    String toJsonString();
}
