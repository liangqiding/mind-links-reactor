package com.mind.links.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.mind.links.common.enums.LinksContentTypeEnum;
import com.mind.links.common.response.ResponseResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * description : TODO
 *
 * @author : qiDing
 * date: 2020-12-24 11:24
 * @version v1.0.0
 */
@RestController
public class TestController {
    @RequestMapping("/users/login")
    public ResponseResult<JSONObject> login() {
        JSONObject body = new JSONObject();
        body.put("accessToken","token123456789token");
        return new ResponseResult<>(body);
    }
    @PostMapping("/users/info")
    public ResponseResult<JSONObject> usersInfo() {
        JSONObject body = new JSONObject();
        JSONObject user = new JSONObject();
//        roles, name, avatar, introduction
        user.put("roles","admin");
        user.put("name","lqd");
        user.put("avatar","");
        user.put("introduction","简介");
        body.put("user",user);
        return new ResponseResult<>(body);
    }

    public static void main(String[] args) {
        String s="null.png";
        HashMap<String, String> headers = new HashMap<>();
        LinksContentTypeEnum.setHeaders(s,headers);
        System.out.println(headers);
    }
}
