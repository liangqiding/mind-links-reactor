package com.mind.links.common.exception;


import com.mind.links.common.enums.LinksExceptionEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * @author: QiDing
 * @date : 2020/11/26 0026 10:09
 * description: TODO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LinksException extends RuntimeException {
    private Integer code;
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