package com.mind.links.netty.security;

import com.mind.links.security.domain.User;
import com.mind.links.security.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * description : TODO
 *
 * @author : qiDing
 * date: 2020-12-08 15:22
 * @version v1.0.0
 */
@SpringBootTest
class SecurityApplicationTest {
    @Autowired
    private UserServiceImpl userService;

    @Test
     void test() {
        List<User> list = userService.list();
        System.out.println(list);
    }
}
