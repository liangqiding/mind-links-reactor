package com.mind.links.security.domain;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.util.ArrayList;
import java.util.Date;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * <p>
 *
 * </p>
 *
 * @author qiDing
 * @since 2020-12-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LinksUser {

    private static final long serialVersionUID = 1L;

    private Long userId;

    private String username;

    private String password;

    private String account;

    private String avatar;

    private String phone;

    private String sex;

    private Date createTime;

    private Date updateTime;

    private String provinceCode;

    private String cityCode;

    private String areaCode;

    private String townCode;

    private Long updateUser;

    private String place;

    private Boolean isDelete;

    private Boolean enable;



}
