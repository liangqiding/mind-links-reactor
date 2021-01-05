package com.mind.links.security.security;

import com.mind.links.common.exception.LinksException;
import com.mind.links.security.domain.MyUserDetail;
import com.mind.links.security.domain.MyUser;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;

/**
 * @author qiDing
 * date: 2021-01-04 16:09
 * @version v1.0.0
 * description
 */
public class MysqlReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        Mono<MyUser> systemUserMono = findByCode();
        ArrayList<SimpleGrantedAuthority> authorityList0 = new ArrayList<>();
        return systemUserMono
                .switchIfEmpty(Mono.error(() -> new LinksException(40000)))
                .flatMap(systemUser -> findByRoleIds(systemUser)
                        .collect(ArrayList<Long>::new, ArrayList::add))
                .flatMap(roleIdList -> findPermissionById(roleIdList)
                        .collect(ArrayList<Long>::new, ArrayList::add)
                        .flatMap(permissionIdList -> findByPermissionIdList(permissionIdList)
                                .collect(ArrayList<String>::new, ArrayList::add))
                )
                .zipWith(systemUserMono, (authorityList, myUser) -> new MyUserDetail(new MyUser(), new User(myUser.getAccount(), myUser.getPassword(), authorityList0)));
    }

    public Mono<MyUser> findByCode() {
        return Mono.just(new MyUser()).map(user -> user.setAccount("root").setPassword("123456"));
    }

    public Flux<Long> findByRoleIds(MyUser user) {
        return Flux.just(1L, 2L);
    }

    public Flux<Long> findPermissionById(List<Long> id) {
        return Flux.just(1L, 2L);
    }

    public Flux<String> findByPermissionIdList(List<Long> id) {
        return Flux.just("admin", "root");
    }
}
