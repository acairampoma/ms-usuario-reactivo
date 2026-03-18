package com.indra.usuarios.reactive.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Datos para registrar un nuevo usuario")
public record UsuarioRequest(

        @NotBlank(message = "El nombre es requerido")
        @Schema(example = "Juan Rodriguez")
        String name,

        @NotBlank(message = "El correo es requerido")
        @Schema(example = "juan@rodriguez.org")
        String email,

        @NotBlank(message = "La contraseña es requerida")
        @Schema(example = "Hunter2@")
        String password,

        @NotNull(message = "El listado de teléfonos es requerido")
        List<TelefonoRequest> phones) {
}
