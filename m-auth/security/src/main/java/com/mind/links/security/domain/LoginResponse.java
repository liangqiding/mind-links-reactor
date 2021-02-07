package com.mind.links.security.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * date: 2021-02-07 13:27
 * description
 *
 * @author qiDing
 */
@Data
@Accessors(chain = true)
public class LoginResponse implements Serializable {

    private static final long serialVersionUID = -42L;

    @ApiModelProperty("token类型 默认bearer")
    private String tokenType;

    @ApiModelProperty("授权token")
    private String accessToken;

    @ApiModelProperty("用于刷新授权token的有效时间")
    private String refreshToken;

    public LoginResponse(String tokenType, String accessToken) {
        this.tokenType = tokenType;
        this.accessToken = accessToken;
    }

    public LoginResponse() {
    }
}
