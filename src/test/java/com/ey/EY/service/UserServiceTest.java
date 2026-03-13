package com.ey.EY.service;

import com.ey.EY.dto.PhoneRequest;
import com.ey.EY.dto.UserRequest;
import com.ey.EY.dto.UserResponse;
import com.ey.EY.exception.EmailAlreadyExistsException;
import com.ey.EY.exception.TokenGenerationException;
import com.ey.EY.model.User;
import com.ey.EY.repository.PhoneRepository;
import com.ey.EY.repository.UserRepository;
import com.ey.EY.config.security.JwtService;
import com.nimbusds.jose.JOSEException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PhoneRepository phoneRepository;
    @Mock private JwtService jwtService;

    @InjectMocks private UserService userService;

    private UserRequest request;

    @BeforeEach
    void setUp() {
        request = new UserRequest(
                "Juan Rodriguez", "juan@dominio.cl", "Hunter12",
                List.of(new PhoneRequest("1234567", "1", "57"))
        );
    }

    @Test
    void register_returnsUserResponse() throws JOSEException {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setName(request.name());
        savedUser.setEmail(request.email());
        savedUser.setToken("mock-token");
        savedUser.setIsactive(true);
        savedUser.setCreated(now);
        savedUser.setModified(now);
        savedUser.setLastLogin(now);

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(jwtService.generateToken(request.email())).thenReturn("mock-token");
        when(userRepository.save(any())).thenReturn(savedUser);

        UserResponse response = userService.register(request);

        assertThat(response.id()).isEqualTo(savedUser.getId());
        assertThat(response.name()).isEqualTo("Juan Rodriguez");
        assertThat(response.token()).isEqualTo("mock-token");
        assertThat(response.isactive()).isTrue();
        assertThat(response.created()).isNotNull();
        assertThat(response.lastLogin()).isEqualTo(response.created());
    }

    @Test
    void register_savesPhones() throws JOSEException {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setToken("mock-token");
        savedUser.setIsactive(true);
        savedUser.setCreated(now);
        savedUser.setModified(now);
        savedUser.setLastLogin(now);

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(jwtService.generateToken(anyString())).thenReturn("mock-token");
        when(userRepository.save(any())).thenReturn(savedUser);

        userService.register(request);

        verify(phoneRepository, times(1)).saveAll(any());
    }

    @Test
    void register_emptyPhones_doesNotSavePhones() throws JOSEException {
        UserRequest requestNoPhones = new UserRequest(
                "Juan Rodriguez", "juan@dominio.cl", "Hunter12", List.of()
        );

        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setToken("mock-token");
        savedUser.setIsactive(true);
        savedUser.setCreated(now);
        savedUser.setModified(now);
        savedUser.setLastLogin(now);

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(jwtService.generateToken(anyString())).thenReturn("mock-token");
        when(userRepository.save(any())).thenReturn(savedUser);

        userService.register(requestNoPhones);

        verify(phoneRepository, never()).saveAll(any());
    }

    @Test
    void register_duplicateEmail_throwsEmailAlreadyExistsException() {
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_tokenError_throwsTokenGenerationException() throws JOSEException {
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(jwtService.generateToken(anyString())).thenThrow(new JOSEException("error"));

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(TokenGenerationException.class);

        verify(userRepository, never()).save(any());
    }
}
