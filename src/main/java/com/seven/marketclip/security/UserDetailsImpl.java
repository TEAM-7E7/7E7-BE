package com.week2.magazine.security;

import com.week2.magazine.account.AccountRoleEnum;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.ArrayList;
import java.util.Collection;

public class UserDetailsImpl implements UserDetails {

    private Long id;

    private String email;

    private String password;

    @Enumerated(value = EnumType.STRING)
    private AccountRoleEnum role;

    public UserDetailsImpl(Long id, String password, String email,AccountRoleEnum role){
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
    }
    public UserDetailsImpl(Long id, String email, AccountRoleEnum role){
        this.id = id;
        this.email = email;
        this.role = role;
    }

    public Long getId(){
        return this.id;
    }
    public AccountRoleEnum getRole(){
        return this.role;
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
}