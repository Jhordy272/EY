package com.ey.EY.controller;

import com.ey.EY.dto.UserRequest;
import com.ey.EY.dto.UserResponse;
import com.ey.EY.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "Registro de usuarios")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario y retorna su información con token de acceso")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario creado exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": "550e8400-e29b-41d4-a716-446655440000",
                                      "name": "Juan Rodriguez",
                                      "email": "juan@dominio.cl",
                                      "phones": [
                                        {
                                          "number": "1234567",
                                          "citycode": "1",
                                          "contrycode": "57"
                                        }
                                      ],
                                      "created": "2026-03-13T10:30:00",
                                      "modified": "2026-03-13T10:30:00",
                                      "last_login": "2026-03-13T10:30:00",
                                      "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWFuQGRvbWluaW8uY2wifQ.abc123",
                                      "isactive": true
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos — puede ser: nombre requerido, correo inválido, contraseña débil, teléfonos requeridos o campos de teléfono vacíos",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(name = "Nombre requerido",       value = """
                                            { "mensaje": "El nombre es requerido" }
                                            """),
                                    @ExampleObject(name = "Correo requerido",       value = """
                                            { "mensaje": "El correo es requerido" }
                                            """),
                                    @ExampleObject(name = "Correo inválido",        value = """
                                            { "mensaje": "El correo debe pertenecer al dominio dominio.cl" }
                                            """),
                                    @ExampleObject(name = "Contraseña requerida",   value = """
                                            { "mensaje": "La contraseña es requerida" }
                                            """),
                                    @ExampleObject(name = "Contraseña débil",       value = """
                                            { "mensaje": "La contraseña debe tener al menos una mayúscula, letras minúsculas y dos números" }
                                            """),
                                    @ExampleObject(name = "Teléfonos requeridos",   value = """
                                            { "mensaje": "Debe incluir al menos un teléfono" }
                                            """),
                                    @ExampleObject(name = "Número requerido",       value = """
                                            { "mensaje": "El número es requerido" }
                                            """),
                                    @ExampleObject(name = "Código ciudad requerido", value = """
                                            { "mensaje": "El código de ciudad es requerido" }
                                            """),
                                    @ExampleObject(name = "Código país requerido",  value = """
                                            { "mensaje": "El código de país es requerido" }
                                            """)
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El correo ya está registrado",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "mensaje": "El correo ya registrado"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "mensaje": "Error al generar el token de acceso"
                                    }
                                    """)
                    )
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody UserRequest request) {
        return userService.register(request);
    }
}
