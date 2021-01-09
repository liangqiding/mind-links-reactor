package com.mind.links.security.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @author qiding
 */
@Data
@ApiModel("自定义用户类")
public class MyUserDetail implements UserDetails, CredentialsContainer {

    private static final long serialVersionUID = 3497935890426858541L;

    @ApiModelProperty("SECURITY 默认的用户类")
    private final User user;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("客户端id")
    private String clientId;

    public MyUserDetail(User user, Long userId) {
        this.user = user;
        this.userId = userId;
    }

    @Override
    public void eraseCredentials() {
        user.eraseCredentials();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

}
