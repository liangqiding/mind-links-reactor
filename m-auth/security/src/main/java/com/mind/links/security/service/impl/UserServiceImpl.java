package com.mind.links.security.service.impl;

import com.mind.links.security.domain.User;
import com.mind.links.security.dao.UserMapper;
import com.mind.links.security.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 * @author qiDing
 * @since 2020-12-09
 */
@Service
@CacheConfig(cacheNames = "user")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    @Cacheable(key = "#root.methodName+'-'+#p0")
    public List<User> listUsers(Long id) {
        return list();
    }
}
