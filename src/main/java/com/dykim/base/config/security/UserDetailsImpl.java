package com.dykim.base.config.security;

import com.dykim.base.entity.member.Member;
import java.util.ArrayList;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final Member member;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority simpleGrantedAuthority =
                new SimpleGrantedAuthority(member.getRoleList());
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);
        return authorities;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return member.getRoleList().contains("ADMIN");
    }

    @Override
    public boolean isAccountNonLocked() {
        return member.getRoleList().contains("ADMIN");
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return member.getRoleList().contains("ADMIN");
    }

    @Override
    public boolean isEnabled() {
        return member.getRoleList().contains("ADMIN");
    }
}
