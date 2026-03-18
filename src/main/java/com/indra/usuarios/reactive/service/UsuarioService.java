package com.indra.usuarios.reactive.service;

import com.indra.usuarios.reactive.dto.UsuarioRequest;
import com.indra.usuarios.reactive.dto.UsuarioResponse;
import reactor.core.publisher.Mono;

// Contrato reactivo de la capa de servicio
public interface UsuarioService {

    Mono<UsuarioResponse> registrar(UsuarioRequest request);
}
