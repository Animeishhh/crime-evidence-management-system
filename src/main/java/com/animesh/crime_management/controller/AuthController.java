package com.animesh.crime_management.controller;

import com.animesh.crime_management.model.User;
import com.animesh.crime_management.repository.UserRepository;
import com.animesh.crime_management.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public static class AuthRequest {
        public String username; // can be name or email
        public String password;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.username, authRequest.password)
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.username);
        User user = userRepository.findByName(userDetails.getUsername())
                .orElseGet(() -> userRepository.findByEmail(userDetails.getUsername()).orElseThrow());
                
        final String jwt = jwtUtil.generateToken(userDetails.getUsername(), user.getRole());

        Map<String, String> response = new HashMap<>();
        response.put("token", jwt);
        response.put("role", user.getRole());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("VIEWER"); // Default role
        } else {
            user.setRole(user.getRole().toUpperCase());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }
}
