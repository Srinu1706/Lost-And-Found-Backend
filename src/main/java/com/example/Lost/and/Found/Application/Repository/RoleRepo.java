package com.example.Lost.and.Found.Application.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Lost.and.Found.Application.Dto.Role;

public interface RoleRepo extends JpaRepository<Role, String> {

}
