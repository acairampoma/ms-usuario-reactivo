package com.indra.usuarios.reactive.controller;

import com.indra.usuarios.reactive.dto.TelefonoRequest;
import com.indra.usuarios.reactive.dto.UsuarioRequest;
import com.indra.usuarios.reactive.dto.UsuarioResponse;
import com.indra.usuarios.reactive.exception.EmailDuplicadoException;
import com.indra.usuarios.reactive.config.SecurityConfig;
import com.indra.usuarios.reactive.exception.GlobalExceptionHandler;
import com.indra.usuarios.reactive.exception.ValidacionException;
import com.indra.usuarios.reactive.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(UsuarioController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class UsuarioControllerTest {

    @Autowired private WebTestClient  webTestClient;
    @MockBean  private UsuarioService usuarioService;

    @Test
    @DisplayName("POST /registro — payload válido — responde 201 con token y isactive")
    void registrar_WhenValidPayload_ShouldReturn201WithTokenAndIsActive() {
        // Arrange
        when(usuarioService.registrar(any())).thenReturn(Mono.just(buildResponse()));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(buildRequest())
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.token").isEqualTo("jwt-token")
                .jsonPath("$.isactive").isEqualTo(true)
                .jsonPath("$.id").exists();
    }

    @Test
    @DisplayName("POST /registro — email duplicado — responde 409 con mensaje exacto")
    void registrar_WhenEmailDuplicado_ShouldReturn409WithExactMessage() {
        // Arrange
        when(usuarioService.registrar(any())).thenReturn(Mono.error(new EmailDuplicadoException()));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(buildRequest())
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.mensaje").isEqualTo("El correo ya registrado");
    }

    @Test
    @DisplayName("POST /registro — contraseña débil — responde 400 con mensaje")
    void registrar_WhenPasswordWeak_ShouldReturn400WithMessage() {
        // Arrange
        when(usuarioService.registrar(any()))
                .thenReturn(Mono.error(new ValidacionException("La contraseña no cumple los requisitos de seguridad")));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(buildRequest())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.mensaje").exists();
    }

    @Test
    @DisplayName("POST /registro — nombre ausente — responde 400 por bean validation")
    void registrar_WhenNameMissing_ShouldReturn400ByBeanValidation() {
        // Arrange — payload sin campo name
        var payload = """
                {"email":"juan@rodriguez.org","password":"Hunter2@","phones":[]}
                """;

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.mensaje").exists();
    }

    private UsuarioRequest buildRequest() {
        return new UsuarioRequest(
                "Juan Rodriguez", "juan@rodriguez.org", "Hunter2@",
                List.of(new TelefonoRequest("1234567", "1", "57")));
    }

    private UsuarioResponse buildResponse() {
        return new UsuarioResponse(
                UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now(),
                LocalDateTime.now(), "jwt-token", true);
    }
}
