package com.mind.links.security.controller;

import com.mind.links.common.response.ResponseResult;
import com.mind.links.security.domain.MyUser;
import com.mind.links.security.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qiDing
 * @since 2020-12-09
 */
@RestController
@Slf4j
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/")
    public ResponseResult<List<MyUser>> test() {
        return new ResponseResult<>(userService.listUsers(666L));
    }
}

