package com.mind.links.security.security.manage;


import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AbstractUserDetailsReactiveAuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * date: 2021-01-07 08:44
 * description
 *
 * @author qiDing
 */
@Component
@Primary
public class ManageAuthenticationManager extends AbstractUserDetailsReactiveAuthenticationManager {
    private Scheduler scheduler = Schedulers.boundedElastic();
    @Autowired
    private ManageDetailsServiceImpl manageDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        System.out.println("authentication:"+JSON.toJSONString(authentication));
        String username = authentication.getName();
        String password = String.valueOf(authentication.getCredentials());
        System.out.println("3-username:"+username);
        System.out.println("4-password:"+password);
        return retrieveUser(username)
                .publishOn(scheduler)
                .filter(u -> passwordEncoder.matches(password, passwordEncoder.encode(u.getPassword())))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BadCredentialsException("账号或密码错误！"))))
                .map(u-> new UsernamePasswordAuthenticationToken(u, u.getPassword()));
    }

    @Override
    protected Mono<UserDetails> retrieveUser(String username) {
        return manageDetailsService.findByUsername(username);
    }
}


