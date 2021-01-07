package com.mind.links.security.service.impl;

import com.mind.links.security.dao.UserMapper;
import com.mind.links.security.domain.MyUser;
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
//@CacheConfig(cacheNames = "user")
public class UserServiceImpl extends ServiceImpl<UserMapper, MyUser> implements IUserService {

    @Override
//    @Cacheable(key = "#root.methodName+'-'+#p0")
    public List<MyUser> listUsers(Long id) {
        return list();
    }
}
