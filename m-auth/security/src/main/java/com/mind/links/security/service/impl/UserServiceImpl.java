package com.mind.links.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mind.links.security.dao.UserMapper;
import com.mind.links.security.domain.LinksUser;
import com.mind.links.security.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import javax.annotation.Resource;

/**
 * <p>
 * 服务实现类  jdbc查询为阻塞的,必须采用fromCallable让其异步化,但实际数据查询还是阻塞的,想不阻塞可以考虑响应式的 r2dbc
 * </p>
 *
 * @author qiDing
 * @since 2020-12-09
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, LinksUser> implements IUserService {

    @Resource
    Scheduler myScheduler;

    @Override
    public Mono<LinksUser> getUserByUsername(String username) {
        return Mono.fromCallable(() -> this.getOne(new QueryWrapper<LinksUser>().eq("username", username))).subscribeOn(myScheduler);
    }
}
