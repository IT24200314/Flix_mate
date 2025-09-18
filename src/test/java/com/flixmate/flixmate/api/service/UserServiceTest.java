package com.flixmate.flixmate.api.service;

import com.flixmate.flixmate.api.entity.User;
import com.flixmate.flixmate.api.entity.UserStatus;
import com.flixmate.flixmate.api.model.ProfileDTO;
import com.flixmate.flixmate.api.model.RegistrationRequest;
import com.flixmate.flixmate.api.repository.UserRepository;
import com.flixmate.flixmate.api.repository.UserStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserStatusRepository userStatusRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_Success_NewStatus() {
        // Arrange
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setUserName("Test User");
        request.setStatusName("PREMIUM");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userStatusRepository.findByStatusName("PREMIUM")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userStatusRepository.save(any(UserStatus.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = userService.registerUser(request);

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals("Test User", result.getUserName());
        assertNotNull(result.getStatus());
        assertEquals("PREMIUM", result.getStatus().getStatusName());

        verify(userStatusRepository).save(any(UserStatus.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_Success_ExistingStatus() {
        // Arrange
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setUserName("Test User");
        request.setStatusName("STANDARD");

        UserStatus existingStatus = new UserStatus("STANDARD");
        existingStatus.setStatusId(1);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userStatusRepository.findByStatusName("STANDARD")).thenReturn(Optional.of(existingStatus));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = userService.registerUser(request);

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals("Test User", result.getUserName());
        assertEquals(existingStatus, result.getStatus());

        verify(userStatusRepository, never()).save(any(UserStatus.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_EmailAlreadyExists() {
        // Arrange
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("existing@example.com");
        request.setPassword("password123");
        request.setUserName("Test User");
        request.setStatusName("STANDARD");

        User existingUser = new User();
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.registerUser(request));
        assertEquals("Email already in use", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getProfile_Success() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setUserName("Test User");
        user.setPhone("1234567890");
        
        UserStatus status = new UserStatus("PREMIUM");
        user.setStatus(status);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        ProfileDTO result = userService.getProfile(email);

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getUserName());
        assertEquals("1234567890", result.getPhone());
        assertEquals("PREMIUM", result.getStatusName());
    }

    @Test
    void getProfile_UserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.getProfile(email));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void updateProfile_Success() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setUserName("Old Name");
        user.setPhone("1111111111");

        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setUserName("New Name");
        profileDTO.setPhone("2222222222");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = userService.updateProfile(email, profileDTO);

        // Assert
        assertNotNull(result);
        assertEquals("New Name", result.getUserName());
        assertEquals("2222222222", result.getPhone());
        verify(userRepository).save(user);
    }

    @Test
    void updateProfile_UserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        ProfileDTO profileDTO = new ProfileDTO();
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.updateProfile(email, profileDTO));
        assertEquals("User not found", exception.getMessage());
    }
}
