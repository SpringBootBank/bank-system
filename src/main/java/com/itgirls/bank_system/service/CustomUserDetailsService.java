package com.itgirls.bank_system.service;

import com.itgirls.bank_system.model.User;
import com.itgirls.bank_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername (String email) throws UsernameNotFoundException {
        User foundUser = userRepository.findUserByEmail(email);
        return org.springframework.security.core.userdetails.User
                .builder()
                .username(foundUser.getEmail())
                .password(foundUser.getPassword())
                .roles(String.valueOf(foundUser.getRole()))
                .build();
    }
}
