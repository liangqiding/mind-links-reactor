package com.mind.links.security.service;

import com.mind.links.security.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.javassist.tools.rmi.Proxy;

import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author qiDing
 * @since 2020-12-09
 */
public interface IUserService extends IService<User> {
    List<User> listUsers(Long id);
}
