package com.prodapt.netinsight.Authentication.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import com.prodapt.netinsight.Authentication.Entity.UserInfo;

/**
 *Custom implementation of UserDetails for the UserInfo entity.
 *This class provides the necessary details for user authentication and authorization.
 */
public class UserInfoUserDetails implements UserDetails{
    
    private String name;
    private String password;
    private List<GrantedAuthority> authorityList;

    /**
     *Constructs a new UserInfoUserDetails object based on the provided UserInfo entity.
     *@param userInfo The UserInfo entity representing the user.
     */
    public UserInfoUserDetails(UserInfo userInfo){
        name=userInfo.getName();
        password=userInfo.getPassword();
        authorityList= Arrays.stream(userInfo.getRole().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorityList;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
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
    
}
