package com.prodapt.netinsight.Authentication.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.prodapt.netinsight.Authentication.Entity.UserInfo;
import com.prodapt.netinsight.Authentication.Repository.UserInfoRepo;

/**
 *Custom implementation of UserDetailsService.
 *This class retrieves UserDetails based on the provided username from the UserInfoRepo repository.
 */
@Component
public class UserInfoDetailsService implements UserDetailsService {
    @Autowired
    private UserInfoRepo repo;

    /**
     *Retrieves a UserDetails object based on the provided username.
     *@param name The username to retrieve the UserDetails for.
     *@return return A UserDetails object representing the User with the given username.
     *@throws UsernameNotFoundException if the User with the given username is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Optional<UserInfo> userInfo=repo.findByName(name);
        return userInfo.map(UserInfoUserDetails::new)
                .orElseThrow(()-> new UsernameNotFoundException("User not found: "+name));
    }
    
}