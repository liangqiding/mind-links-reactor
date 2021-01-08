package com.mind.links.security.service;

import com.mind.links.security.domain.LinksUser;
import com.baomidou.mybatisplus.extension.service.IService;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author qiDing
 * @since 2020-12-09
 */
public interface IUserService extends IService<LinksUser> {
    /**
     * 查询用户信息
     *
     * @param account 账号
     * @return 用户
     */
    Mono<LinksUser> getUserByUsername(String account);
}
