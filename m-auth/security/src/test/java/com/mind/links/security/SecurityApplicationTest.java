package com.mind.links.security;


import com.mind.links.security.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * description : TODO
 *
 * @author : qiDing
 * date: 2020-12-08 15:22
 * @version v1.0.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityApplicationTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
     void test() {
        String encode = passwordEncoder.encode("123456");
        System.out.println(encode);
    }

}
