package com.mind.links.common.exception;


import com.mind.links.common.enums.LinksExceptionEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * @author: QiDing
 * @date : 2020/11/26 0026 10:09
 * description: TODO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("自定义异常")
public class LinksException extends RuntimeException {

    @ApiModelProperty("错误码")
    private Integer code;

    @ApiModelProperty("错误反馈")
    private String msg;

    public LinksException(Integer code, String msg){
        super(msg);
        this.code = code;
        this.msg = msg;
    }
    public LinksException(Integer code){
        super(LinksExceptionEnum.getMsgByCode(code));
        this.code = code;
        this.msg = LinksExceptionEnum.getMsgByCode(code);
    }
    public LinksException(String msg){
        super(msg);
        this.msg = msg;
    }
}