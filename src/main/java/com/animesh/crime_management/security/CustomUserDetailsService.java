package com.animesh.crime_management.security;

import com.animesh.crime_management.model.User;
import com.animesh.crime_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Trying by Name first, then falling back to Email if necessary
        User user = userRepository.findByName(username)
                .orElseGet(() -> userRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username)));

        if ("INACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new DisabledException("User account is inactive");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getName(), 
                user.getPassword(), 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()))
        );
    }
}
