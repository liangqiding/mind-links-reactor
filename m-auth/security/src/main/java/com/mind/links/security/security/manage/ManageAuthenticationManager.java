package com.mind.links.security.security.manage;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ManageAuthenticationManager extends AbstractUserDetailsReactiveAuthenticationManager {

    private final static Scheduler SCHEDULER = Schedulers.boundedElastic();

    @Autowired
    private ManageDetailsServiceImpl manageDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Mono<Authentication> authenticate(final Authentication authentication) {
        logger.debug("authentication:" + JSON.toJSONString(authentication));
        if (authentication.isAuthenticated()) {
            return Mono.just(authentication);
        }
        return retrieveUser(authentication.getName())
                .publishOn(SCHEDULER)
                .filter(u -> passwordEncoder.matches(String.valueOf(authentication.getCredentials()), passwordEncoder.encode(u.getPassword())))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BadCredentialsException("账号或密码错误！"))))
                .map(u -> new UsernamePasswordAuthenticationToken(u, u.getPassword(),u.getAuthorities()));
    }

    @Override
    protected Mono<UserDetails> retrieveUser(String username) {
        return manageDetailsService.findByUsername(username);
    }
}


