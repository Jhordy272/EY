package com.ey.EY.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Datos de un teléfono")
public record PhoneRequest(
        @Schema(example = "1234567", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El número es requerido")
        String number,

        @Schema(example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El código de ciudad es requerido")
        String citycode,

        @Schema(example = "57", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El código de país es requerido")
        String contrycode
) {}
