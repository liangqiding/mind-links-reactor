package links.common.controller;


import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author qiDing
 * @since 2020-12-03
 */
@RestController
public class UserController {
    @RequestMapping("/")
    public String test() {
         return "66666";
    }
}

