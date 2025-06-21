package com.example.Lost.and.Found.Application.Controller;

import com.example.Lost.and.Found.Application.Dto.*;
import com.example.Lost.and.Found.Application.Repository.RoleRepo;
import com.example.Lost.and.Found.Application.Repository.UserRepository;
import com.example.Lost.and.Found.Application.Service.EmailService;
import com.example.Lost.and.Found.Application.Service.OtpService;
import com.example.Lost.and.Found.Application.Service.UserService;
import com.example.Lost.and.Found.Application.jwt.Jwtutil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
public class AuthController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepo roleRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private Jwtutil jwtUtil;

	@Autowired
	private UserService userService;

	@Autowired
	private OtpService otpService;

	@Autowired
	private EmailService emailService;

	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody RegistrationRequest request) {
		try {
			if (request.getUsername() == null || request.getUsername().isEmpty()) {
				return ResponseEntity.badRequest().body("Username is required");
			}
			if (request.getPassword() == null || request.getPassword().isEmpty()) {
				return ResponseEntity.badRequest().body("Password is required");
			}
			if (userRepository.findByUsername(request.getUsername()).isPresent()) {
				return ResponseEntity.badRequest().body("Username already exists");
			}

			User user = new User();
			user.setUsername(request.getUsername());
			user.setPassword(passwordEncoder.encode(request.getPassword()));
			 user.setEnabled(false); 

			Set<Role> userRoles = new HashSet<>();
			if (request.getRoles() != null && !request.getRoles().isEmpty()) {
				for (String roleName : request.getRoles()) {
					Role role = roleRepo.findById(roleName).orElseGet(() -> {
						Role newRole = new Role(roleName);
						return roleRepo.save(newRole);
					});
					userRoles.add(role);
				}
			} else {
				Role role = roleRepo.findById("ROLE_USER").orElseGet(() -> {
					Role newRole = new Role("ROLE_USER");
					return roleRepo.save(newRole);
				});
				userRoles.add(role);
			}
			user.setRoles(userRoles);
			userRepository.save(user);
			String otp = otpService.generateOtp(user.getUsername());
			emailService.sendOtpEmail(user.getUsername(), otp);
			System.out.println("Generated OTP: " + otp);


			return ResponseEntity.ok("User registered successfully!");
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Registration failed: " + e.getMessage());
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {
	    try {
	        Authentication auth = authenticationManager.authenticate(
	                new UsernamePasswordAuthenticationToken(
	                        request.getUsername(), request.getPassword()
	                )
	        );

	        UserDetails userDetails = userService.loadUserByUsername(request.getUsername());

	        if (!userDetails.isEnabled()) {
	            return ResponseEntity.status(403).body("Account not activated. Please verify your email OTP.");
	        }

	        String token = jwtUtil.generateToken(userDetails);
	        return ResponseEntity.ok().body("JWT Token: " + token);

	    } catch (Exception e) {
	        return ResponseEntity.status(401).body("Invalid username or password");
	    }
	}

	
	@PostMapping("/verify-otp")
	public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
	    boolean valid = otpService.validateOtp(email, otp);

	    if (!valid) {
	        return ResponseEntity.badRequest().body("Invalid or expired OTP");
	    }

	    User user = userRepository.findByUsername(email).orElse(null);
	    if (user == null) {
	        return ResponseEntity.badRequest().body("User not found");
	    }

	    user.setEnabled(true); 
	    userRepository.save(user);
	    otpService.clearOtp(email); 

	    return ResponseEntity.ok("OTP verified. Account activated.");
	}


}
