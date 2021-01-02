package com.mind.links.netty.controller;


import com.mind.links.netty.service.RedissonServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * description : TODO
 *
 * @author : qiDing
 * date: 2020-12-10 11:40
 * @version v1.0.0
 */
@RestController
public class IndexController {
    @Autowired
    RedissonServiceImp redissonServiceImp;

    @GetMapping("/save")
    public String save() {
        redissonServiceImp.saveObject();
        return "success";
    }
}
