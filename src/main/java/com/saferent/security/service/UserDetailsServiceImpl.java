package com.saferent.security.service;

import com.saferent.domain.User;
import com.saferent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl {

    @Autowired
    private UserService userService;

    @Autowired
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        User user = userService.getUserByEmail(email);
        return UserDetailsImpl.build(user);
    }
}
