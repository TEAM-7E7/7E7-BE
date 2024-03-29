package com.seven.marketclip.security;


import com.seven.marketclip.account.repository.AccountRoleEnum;
import com.seven.marketclip.account.repository.AccountTypeEnum;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@NoArgsConstructor
public class UserDetailsImpl implements UserDetails, OAuth2User {

    private Long id;

    private String nickname;
    private String email;

    private String password;

    private String profileImgUrl;

    @Enumerated(value = EnumType.STRING)
    private AccountTypeEnum type;

    @Enumerated(value = EnumType.STRING)
    private AccountRoleEnum role;

    @Builder
    public UserDetailsImpl(Long id, String password, String nickname, String email,AccountTypeEnum type, String profileImgUrl, AccountRoleEnum role) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
        this.type = type;
        this.profileImgUrl = profileImgUrl;
    }


    public Long getId() {
        return this.id;
    }

    public AccountRoleEnum getRole() {
        return this.role;
    }
    public AccountTypeEnum getType() {
        return this.type;
    }

    public String getNickname() {
        return this.nickname;
    }

    public String getProfileImgUrl() {
        return this.profileImgUrl;
    }


    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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