package com.mind.links.security.service;

import com.mind.links.security.domain.LinksUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mind.links.security.domain.vo.LinksUserVo;
import org.springframework.security.core.userdetails.User;
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

    /**
     * 查询用户详细信息
     *
     * @param userId 用户id
     * @return userinfo
     */
    Mono<LinksUserVo> getUserById(Long userId);


}
