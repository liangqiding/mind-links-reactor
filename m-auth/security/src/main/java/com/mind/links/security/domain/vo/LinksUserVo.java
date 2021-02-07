package com.mind.links.security.domain.vo;


import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.mind.links.security.domain.LinksUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.util.Date;


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
public class LinksUserVo extends Model<LinksUserVo> {

    private static final long serialVersionUID = 1L;

    public LinksUserVo(LinksUser linksUser) {
        this.userId = linksUser.getUserId();
        this.username = linksUser.getUsername();
        this.name = linksUser.getName();
        this.avatar = linksUser.getAvatar();
        this.phone = linksUser.getPhone();
        this.sex = linksUser.getSex();
        this.createTime = linksUser.getCreateTime();
        this.updateTime = linksUser.getUpdateTime();
        this.provinceCode = linksUser.getProvinceCode();
        this.cityCode = linksUser.getCityCode();
        this.areaCode = linksUser.getAreaCode();
        this.townCode = linksUser.getTownCode();
        this.place = linksUser.getPlace();
        this.introduction = linksUser.getIntroduction();
    }
    @ApiModelProperty("用户权限")
    private String roles="[\"admin\"]";

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("账号")
    private String username;

    @ApiModelProperty("个人说明")
    private String introduction;

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

    @ApiModelProperty("地址")
    private String place;

    @Override
    protected Serializable pkVal() {
        return super.pkVal();
    }
}
