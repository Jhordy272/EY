package com.ey.EY.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Respuesta al registrar un usuario")
public record UserResponse(
        @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(example = "Juan Rodriguez")
        String name,

        @Schema(example = "juan@dominio.cl")
        String email,

        List<PhoneResponse> phones,

        @Schema(example = "2026-03-13T10:30:00")
        LocalDateTime created,

        @Schema(example = "2026-03-13T10:30:00")
        LocalDateTime modified,

        @JsonProperty("last_login")
        @Schema(example = "2026-03-13T10:30:00")
        LocalDateTime lastLogin,

        @Schema(example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWFuQGRvbWluaW8uY2wifQ.abc123")
        String token,

        @Schema(example = "true")
        boolean isactive
) {}
