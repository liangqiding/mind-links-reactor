package com.mind.links.security.service.impl;

import com.mind.links.security.domain.User;
import com.mind.links.security.dao.UserMapper;
import com.mind.links.security.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author qiDing
 * @since 2020-12-09
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
