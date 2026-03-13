package com.ey.EY.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de un teléfono")
public record PhoneResponse(
        @Schema(example = "1234567") String number,
        @Schema(example = "1")       String citycode,
        @Schema(example = "57")      String contrycode
) {}
