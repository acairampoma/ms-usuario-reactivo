package com.indra.usuarios.reactive.service;

import com.indra.usuarios.reactive.config.ValidationProperties;
import com.indra.usuarios.reactive.dto.TelefonoRequest;
import com.indra.usuarios.reactive.dto.UsuarioRequest;
import com.indra.usuarios.reactive.entity.Usuario;
import com.indra.usuarios.reactive.exception.EmailDuplicadoException;
import com.indra.usuarios.reactive.exception.ValidacionException;
import com.indra.usuarios.reactive.repository.TelefonoRepository;
import com.indra.usuarios.reactive.repository.UsuarioRepository;
import com.indra.usuarios.reactive.security.JwtService;
import com.indra.usuarios.reactive.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String PASS_REGEX  = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[a-zA-Z0-9@$!%*?&]{8,}$";
    private static final String VALID_EMAIL = "juan@rodriguez.org";
    private static final String VALID_PASS  = "Hunter2@";

    @Mock private UsuarioRepository    usuarioRepository;
    @Mock private TelefonoRepository   telefonoRepository;
    @Mock private PasswordEncoder      passwordEncoder;
    @Mock private JwtService           jwtService;
    @Mock private ValidationProperties validationProperties;

    @InjectMocks
    private UsuarioServiceImpl service;

    @BeforeEach
    void setUp() {
        // lenient — algún test falla antes de usar ambos stubs (ej: email inválido no llega a password)
        lenient().when(validationProperties.emailRegex()).thenReturn(EMAIL_REGEX);
        lenient().when(validationProperties.passwordRegex()).thenReturn(PASS_REGEX);
    }

    @Test
    @DisplayName("registrar — datos válidos — emite UsuarioResponse con token")
    void registrar_WhenValidData_ShouldEmitResponseWithToken() {
        // Arrange
        var saved = buildUsuario("jwt-token");
        when(usuarioRepository.existsByEmailIgnoreCase(VALID_EMAIL)).thenReturn(Mono.just(false));
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(jwtService.generate(VALID_EMAIL)).thenReturn("jwt-token");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(Mono.just(saved));
        when(telefonoRepository.saveAll(any(Iterable.class))).thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(service.registrar(buildRequest(VALID_EMAIL, VALID_PASS)))
                .expectNextMatches(r -> r.token().equals("jwt-token") && r.isactive())
                .verifyComplete();
    }

    @Test
    @DisplayName("registrar — email duplicado — emite EmailDuplicadoException con mensaje exacto")
    void registrar_WhenEmailDuplicado_ShouldEmitExceptionWithExactMessage() {
        // Arrange
        when(usuarioRepository.existsByEmailIgnoreCase(VALID_EMAIL)).thenReturn(Mono.just(true));

        // Act & Assert
        StepVerifier.create(service.registrar(buildRequest(VALID_EMAIL, VALID_PASS)))
                .expectErrorMatches(ex ->
                        ex instanceof EmailDuplicadoException
                        && "El correo ya registrado".equals(ex.getMessage()))
                .verify();
    }

    @Test
    @DisplayName("registrar — email inválido — emite ValidacionException sin consultar BD")
    void registrar_WhenEmailInvalid_ShouldEmitValidacionErrorImmediately() {
        // Arrange — email sin formato válido

        // Act & Assert
        StepVerifier.create(service.registrar(buildRequest("no-es-email", VALID_PASS)))
                .expectErrorMatches(ex ->
                        ex instanceof ValidacionException
                        && ex.getMessage().contains("correo"))
                .verify();
    }

    @Test
    @DisplayName("registrar — contraseña débil — emite ValidacionException")
    void registrar_WhenPasswordWeak_ShouldEmitValidacionException() {
        // Arrange

        // Act & Assert
        StepVerifier.create(service.registrar(buildRequest(VALID_EMAIL, "debil")))
                .expectErrorMatches(ex ->
                        ex instanceof ValidacionException
                        && ex.getMessage().contains("contraseña"))
                .verify();
    }

    private UsuarioRequest buildRequest(String email, String password) {
        return new UsuarioRequest(
                "Juan Rodriguez", email, password,
                List.of(new TelefonoRequest("1234567", "1", "57")));
    }

    private Usuario buildUsuario(String token) {
        return Usuario.builder()
                .id(UUID.randomUUID())
                .name("Juan Rodriguez")
                .email(VALID_EMAIL)
                .password("hashed")
                .token(token)
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .isActive(true)
                .build();
    }
}
