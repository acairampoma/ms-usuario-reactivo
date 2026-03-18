package com.indra.usuarios.reactive.service.impl;

import com.indra.usuarios.reactive.config.ValidationProperties;
import com.indra.usuarios.reactive.dto.UsuarioRequest;
import com.indra.usuarios.reactive.dto.UsuarioResponse;
import com.indra.usuarios.reactive.entity.Telefono;
import com.indra.usuarios.reactive.entity.Usuario;
import com.indra.usuarios.reactive.exception.EmailDuplicadoException;
import com.indra.usuarios.reactive.exception.ValidacionException;
import com.indra.usuarios.reactive.repository.TelefonoRepository;
import com.indra.usuarios.reactive.repository.UsuarioRepository;
import com.indra.usuarios.reactive.security.JwtService;
import com.indra.usuarios.reactive.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository    usuarioRepository;
    private final TelefonoRepository   telefonoRepository;
    private final PasswordEncoder      passwordEncoder;
    private final JwtService           jwtService;
    private final ValidationProperties validationProperties;

    @Override
    @Transactional
    public Mono<UsuarioResponse> registrar(UsuarioRequest request) {
        try {
            validarEmail(request.email());
            validarPassword(request.password());
        } catch (ValidacionException ex) {
            return Mono.error(ex);
        }

        return usuarioRepository.existsByEmailIgnoreCase(request.email())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new EmailDuplicadoException());
                    }
                    return persistirUsuario(request);
                });
    }

    private Mono<UsuarioResponse> persistirUsuario(UsuarioRequest request) {
        var now   = LocalDateTime.now();
        var token = jwtService.generate(request.email());

        // Builder pattern — construcción fluida de la entidad
        var usuario = Usuario.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .token(token)
                .lastLogin(now)
                .isActive(true)
                .build();

        return usuarioRepository.save(usuario)
                .flatMap(saved -> persistirTelefonos(saved, request).then(Mono.just(saved)))
                .doOnSuccess(u -> log.debug("Usuario registrado: id={}", u.getId()))
                // Factory Method — construcción del DTO de respuesta
                .map(UsuarioResponse::from);
    }

    // Adapter — mapea DTOs de teléfonos a entidades R2DBC
    private Mono<Void> persistirTelefonos(Usuario usuario, UsuarioRequest request) {
        if (request.phones() == null || request.phones().isEmpty()) {
            return Mono.empty();
        }
        List<Telefono> telefonos = request.phones().stream()
                .map(p -> Telefono.builder()
                        .usuarioId(usuario.getId())
                        .number(p.number())
                        .citycode(p.citycode())
                        .contrycode(p.contrycode())
                        .build())
                .toList();
        return telefonoRepository.saveAll(telefonos).then();
    }

    private void validarEmail(String email) {
        if (!email.matches(validationProperties.emailRegex())) {
            throw new ValidacionException("El correo no tiene un formato válido");
        }
    }

    private void validarPassword(String password) {
        if (!password.matches(validationProperties.passwordRegex())) {
            throw new ValidacionException("La contraseña no cumple los requisitos de seguridad");
        }
    }
}
