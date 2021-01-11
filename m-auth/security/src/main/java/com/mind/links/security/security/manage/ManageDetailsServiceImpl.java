package com.mind.links.security.security.manage;

import com.mind.links.security.config.LinksAuthException;
import com.mind.links.security.domain.MyUserDetail;
import com.mind.links.security.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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

    @Autowired
    UserServiceImpl userService;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return Mono.just(username)
                .flatMap(s -> userService.getUserByUsername(username))
                .switchIfEmpty(LinksAuthException.errors("账号不存在"))
                .zipWith(getAuthorities(), (linksUser, authorities) -> new MyUserDetail(new User(linksUser.getUsername(), linksUser.getPassword(), authorities), linksUser.getUserId()));
    }

    /**
     * 模拟获取权限
     */
    private Mono<List<GrantedAuthority>> getAuthorities() {
        List<String> userAccess = new ArrayList<>();
        userAccess.add("ROLE_USER");
        userAccess.add("ROLE_ADMIN");
        return Flux.fromIterable(userAccess)
                .collect(ArrayList::new, (authorities, grantedAuthority) -> authorities.add(new SimpleGrantedAuthority(grantedAuthority)));

    }

}


