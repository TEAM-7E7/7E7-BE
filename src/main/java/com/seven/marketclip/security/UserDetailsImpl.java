package com.seven.marketclip.security;


import com.seven.marketclip.account.AccountRoleEnum;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class UserDetailsImpl implements UserDetails, OAuth2User {

    private Long id;
    private String nickname;
    private String email;
    private String password;

    @Enumerated(value = EnumType.STRING)
    private AccountRoleEnum role;

    //로그인 할 때, 필요한
    public UserDetailsImpl(Long id, String password, String nickname, String email, AccountRoleEnum role) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
    }

    //JWT 토큰 암호화, 복호화 때 필요한.
    public UserDetailsImpl(Long id, String email, String nickname, AccountRoleEnum role) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
    }

    //카카오 로그인때 필요한.
    public UserDetailsImpl(String email, String nickname, AccountRoleEnum role) {
        this.email = email;
        this.nickname = nickname;
        this.role = role;
    }

    public Long getId() {
        return this.id;
    }

    public AccountRoleEnum getRole() {
        return this.role;
    }

    public String getEmail() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return nickname;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        AccountRoleEnum role = this.role;
        String authority = role.getAuthority();

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);

        return authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}