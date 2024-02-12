package com.hanghae.newsfeed.auth.security;

import com.hanghae.newsfeed.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String email) {
        return userRepository.findByEmail(email)
                .map(UserDetailsImpl::from)
                .orElseThrow(() -> new IllegalArgumentException("User not found" + email));
    }
}