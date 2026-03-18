package com.indra.usuarios.reactive.repository;

import com.indra.usuarios.reactive.entity.Telefono;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface TelefonoRepository extends ReactiveCrudRepository<Telefono, UUID> {

    Flux<Telefono> findByUsuarioId(UUID usuarioId);
}
