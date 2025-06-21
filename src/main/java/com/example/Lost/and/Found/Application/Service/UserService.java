package com.example.Lost.and.Found.Application.Service;

import com.example.Lost.and.Found.Application.Dto.User;
import com.example.Lost.and.Found.Application.Repository.UserRepository;
import com.example.Lost.and.Found.Application.security.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isPresent()) {
            return new AppUserDetails(optionalUser.get()); 
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
