package com.flixmate.flixmate.api.service;

import com.flixmate.flixmate.api.entity.User;
import com.flixmate.flixmate.api.entity.UserStatus;
import com.flixmate.flixmate.api.model.ProfileDTO;
import com.flixmate.flixmate.api.model.RegistrationRequest;
import com.flixmate.flixmate.api.repository.UserRepository;
import com.flixmate.flixmate.api.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(RegistrationRequest request) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        // Find or create UserStatus
        Optional<UserStatus> statusOpt = userStatusRepository.findByStatusName(request.getStatusName());
        UserStatus status;
        
        if (statusOpt.isPresent()) {
            status = statusOpt.get();
        } else {
            // Create new status if it doesn't exist
            status = new UserStatus(request.getStatusName());
            status = userStatusRepository.save(status);
        }

        // Create and save new user
        User user = new User(request.getEmail(), passwordEncoder.encode(request.getPassword()),
                request.getUserName(), status);
        return userRepository.save(user);
    }

    public ProfileDTO getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new ProfileDTO(user.getEmail(), user.getUserName(), user.getPhone(), user.getStatus().getStatusName());
    }

    public User updateProfile(String email, ProfileDTO profileDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setUserName(profileDTO.getUserName());
        user.setPhone(profileDTO.getPhone());
        // Note: Status and email cannot be updated here for simplicity
        return userRepository.save(user);
    }
}
