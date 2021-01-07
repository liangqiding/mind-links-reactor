package com.mind.links.security.security.manage;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * date: 2021-01-07 08:45
 * description
 *
 * @author qiDing
 */
@Component
public class ManageDetailsServiceImpl implements ReactiveUserDetailsService {

    @Resource
    PasswordEncoder passwordEncoder;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        List<String> userAccess = new ArrayList<>();
        userAccess.add("ROLE_USER");
        userAccess.add("ROLE_ADMIN");
        userAccess.forEach(auth -> authorities.add(new SimpleGrantedAuthority(auth)));
        String encode = passwordEncoder.encode("123456");
        User user = new User(username, "123456", authorities);
        return Mono.just(user);
    }
}


