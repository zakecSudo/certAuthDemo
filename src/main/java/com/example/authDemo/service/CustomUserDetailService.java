package com.example.authDemo.service;

import com.example.authDemo.model.User;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService, UserService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username.equals("Bob")   ) {
            return new org.springframework.security.core.userdetails.User(username, "",
                    AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
        }

        throw new UsernameNotFoundException(String.format("User %s not found!", username));
    }

    @Override
    public User findCurrentUser() {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return new User(user.getUsername(), user.getPassword());
    }
}
