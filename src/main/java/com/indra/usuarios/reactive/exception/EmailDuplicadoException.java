package com.indra.usuarios.reactive.exception;

public class EmailDuplicadoException extends RuntimeException {

    public EmailDuplicadoException() {
        super("El correo ya registrado");
    }
}
