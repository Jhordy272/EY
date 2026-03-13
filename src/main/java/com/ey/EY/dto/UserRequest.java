package com.ey.EY.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.util.List;

@Schema(description = "Datos para registrar un nuevo usuario")
public record UserRequest(
        @Schema(example = "Juan Rodriguez")
        @NotBlank(message = "El nombre es requerido")
        String name,

        @Schema(example = "juan@dominio.cl")
        @NotBlank(message = "El correo es requerido")
        @Pattern(
                regexp = "^[\\w.+-]+@dominio\\.cl$",
                message = "El correo debe pertenecer al dominio dominio.cl"
        )
        String email,

        @Schema(example = "Hunter12")
        @NotBlank(message = "La contraseña es requerida")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=(?:.*\\d){2}).+$",
                message = "La contraseña debe tener al menos una mayúscula, letras minúsculas y dos números"
        )
        String password,

        @NotEmpty(message = "Debe incluir al menos un teléfono")
        List<@Valid PhoneRequest> phones
) {}
