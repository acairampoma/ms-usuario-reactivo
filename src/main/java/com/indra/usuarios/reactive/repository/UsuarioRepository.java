package com.indra.usuarios.reactive.repository;

import com.indra.usuarios.reactive.entity.Usuario;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UsuarioRepository extends ReactiveCrudRepository<Usuario, UUID> {

    Mono<Boolean> existsByEmailIgnoreCase(String email);
}
