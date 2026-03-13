package com.ey.EY.controller;

import com.ey.EY.dto.PhoneRequest;
import com.ey.EY.dto.UserRequest;
import com.ey.EY.dto.UserResponse;
import com.ey.EY.exception.EmailAlreadyExistsException;
import com.ey.EY.exception.GlobalExceptionHandler;
import com.ey.EY.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock private UserService userService;
    @InjectMocks private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void register_validRequest_returns201() throws Exception {
        UserRequest request = new UserRequest(
                "Juan Rodriguez", "juan@dominio.cl", "Hunter12",
                List.of(new PhoneRequest("1234567", "1", "57"))
        );

        LocalDateTime now = LocalDateTime.now();
        when(userService.register(any())).thenReturn(
                new UserResponse(UUID.randomUUID(), "Juan Rodriguez", "juan@dominio.cl",
                        List.of(), now, now, now, "mock-token", true)
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Juan Rodriguez"))
                .andExpect(jsonPath("$.token").value("mock-token"))
                .andExpect(jsonPath("$.isactive").value(true));
    }

    @Test
    void register_duplicateEmail_returns409() throws Exception {
        UserRequest request = new UserRequest(
                "Juan Rodriguez", "juan@dominio.cl", "Hunter12",
                List.of(new PhoneRequest("1234567", "1", "57"))
        );

        when(userService.register(any())).thenThrow(new EmailAlreadyExistsException());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("El correo ya registrado"));
    }

    @Test
    void register_blankName_returns400() throws Exception {
        UserRequest request = new UserRequest(
                "", "juan@dominio.cl", "Hunter12",
                List.of(new PhoneRequest("1234567", "1", "57"))
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El nombre es requerido"));
    }

    @Test
    void register_invalidEmailDomain_returns400() throws Exception {
        UserRequest request = new UserRequest(
                "Juan Rodriguez", "juan@gmail.com", "Hunter12",
                List.of(new PhoneRequest("1234567", "1", "57"))
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El correo debe pertenecer al dominio dominio.cl"));
    }

    @Test
    void register_weakPassword_returns400() throws Exception {
        UserRequest request = new UserRequest(
                "Juan Rodriguez", "juan@dominio.cl", "hunter2",
                List.of(new PhoneRequest("1234567", "1", "57"))
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("La contraseña debe tener al menos una mayúscula, letras minúsculas y dos números"));
    }

    @Test
    void register_blankPhoneNumber_returns400() throws Exception {
        UserRequest request = new UserRequest(
                "Juan Rodriguez", "juan@dominio.cl", "Hunter12",
                List.of(new PhoneRequest("", "1", "57"))
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El número es requerido"));
    }

    @Test
    void register_blankCitycode_returns400() throws Exception {
        UserRequest request = new UserRequest(
                "Juan Rodriguez", "juan@dominio.cl", "Hunter12",
                List.of(new PhoneRequest("1234567", "", "57"))
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El código de ciudad es requerido"));
    }

    @Test
    void register_blankContrycode_returns400() throws Exception {
        UserRequest request = new UserRequest(
                "Juan Rodriguez", "juan@dominio.cl", "Hunter12",
                List.of(new PhoneRequest("1234567", "1", ""))
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El código de país es requerido"));
    }
}
