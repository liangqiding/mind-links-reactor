package links.common.domain;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author qiDing
 * @since 2020-12-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class User extends Model<User> {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
      private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 账号
     */
    private String account;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 手机
     */
    private String phone;

    /**
     * 0男 1女
     */
    private Integer sex;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 省
     */
    @TableField("provinceCode")
    private String provinceCode;

    /**
     * 市
     */
    @TableField("cityCode")
    private String cityCode;

    /**
     * 区
     */
    @TableField("areaCode")
    private String areaCode;

    /**
     * 镇
     */
    @TableField("townCode")
    private String townCode;

    /**
     * 更新人
     */
    private Date updateUser;

    /**
     * 详细地址
     */
    private String place;

    /**
     * 1表示已经删除
     */
    @TableField("isDelete")
    private Boolean isDelete;

    /**
     * 1表示启用
     */
    private Boolean enable;


    @Override
    protected Serializable pkVal() {
        return this.userId;
    }

}
