package com.animesh.crime_management.controller;

import com.animesh.crime_management.model.User;
import com.animesh.crime_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (user.getRole() == null || user.getRole().isEmpty()) user.setRole("VIEWER");
        if (user.getStatus() == null || user.getStatus().isEmpty()) user.setStatus("ACTIVE");
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return ResponseEntity.ok(userRepository.save(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User request) {
        User user = userRepository.findById(id).orElseThrow();
        
        if (request.getRole() != null && !request.getRole().isEmpty()) user.setRole(request.getRole().toUpperCase());
        if (request.getStatus() != null && !request.getStatus().isEmpty()) user.setStatus(request.getStatus().toUpperCase());
        
        return ResponseEntity.ok(userRepository.save(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}