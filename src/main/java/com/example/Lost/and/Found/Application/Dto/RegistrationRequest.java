package com.example.Lost.and.Found.Application.Dto;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationRequest {
    private String username;
    private String password;
    private Set<String> roles;  
}