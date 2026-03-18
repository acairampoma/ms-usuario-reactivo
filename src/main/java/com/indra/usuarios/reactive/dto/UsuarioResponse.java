package com.indra.usuarios.reactive.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.indra.usuarios.reactive.entity.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Respuesta de registro exitoso")
public record UsuarioResponse(

        UUID id,
        LocalDateTime created,
        LocalDateTime modified,

        @JsonProperty("last_login")
        LocalDateTime lastLogin,

        String token,

        @JsonProperty("isactive")
        boolean isactive) {

    // Factory Method — único punto de construcción del DTO desde entidad
    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(
                u.getId(),
                u.getCreated(),
                u.getModified(),
                u.getLastLogin(),
                u.getToken(),
                u.isActive());
    }
}
