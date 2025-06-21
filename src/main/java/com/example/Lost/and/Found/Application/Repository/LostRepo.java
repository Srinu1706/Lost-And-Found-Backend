package com.example.Lost.and.Found.Application.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Lost.and.Found.Application.Dto.Lostitem;
import com.example.Lost.and.Found.Application.Dto.User;
@Repository
public interface LostRepo extends JpaRepository<Lostitem, Integer> {
    Optional<Lostitem> findByRepotername(String repotername);  
}

