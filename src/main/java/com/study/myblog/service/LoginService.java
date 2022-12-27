package com.study.myblog.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.study.myblog.domain.User;
import com.study.myblog.dto.LoginUser;
import com.study.myblog.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class LoginService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println(1);
        Optional<User> userOp = userRepository.findByUsername(username);
        System.out.println(2);
        if (userOp.isPresent()) {
            return new LoginUser(userOp.get());
        }
        System.out.println(3);
        return null;
    }

}
