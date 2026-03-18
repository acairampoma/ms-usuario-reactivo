package com.indra.usuarios.reactive.controller;

import com.indra.usuarios.reactive.dto.UsuarioRequest;
import com.indra.usuarios.reactive.dto.UsuarioResponse;
import com.indra.usuarios.reactive.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

// Facade — oculta la complejidad reactiva (Mono/flatMap) al cliente HTTP
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Registro de usuarios con JWT — versión reactiva")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/registro")
    @Operation(summary = "Registrar nuevo usuario (reactivo)",
               description = "Spring WebFlux + R2DBC. Retorna Mono — sin bloqueo del event loop")
    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "409", description = "El correo ya está registrado")
    public Mono<ResponseEntity<UsuarioResponse>> registrar(@RequestBody @Valid UsuarioRequest request) {
        return usuarioService.registrar(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }
}
