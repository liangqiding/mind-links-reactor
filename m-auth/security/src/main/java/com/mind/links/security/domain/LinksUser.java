package com.mind.links.security.domain;


import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


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
@ApiModel("用户类")
public class LinksUser extends Model<LinksUser> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("账号")
    private String username;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("用户名称")
    private String name;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("手机")
    private String phone;

    @ApiModelProperty("性别")
    private String sex;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("行政码 省")
    private String provinceCode;

    @ApiModelProperty("行政码 市")
    private String cityCode;

    @ApiModelProperty("行政码 区")
    private String areaCode;

    @ApiModelProperty("行政码 镇")
    private String townCode;

    @ApiModelProperty("更新人")
    private Long updateUser;

    @ApiModelProperty("地址")
    private String place;

    @ApiModelProperty("是否已删除")
    private Boolean isDelete;

    @ApiModelProperty("是否启用")
    private Boolean enable;

    @Override
    protected Serializable pkVal() {
        return super.pkVal();
    }
}
