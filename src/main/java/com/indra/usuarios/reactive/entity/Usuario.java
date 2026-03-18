package com.indra.usuarios.reactive.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("usuario")
public class Usuario {

    @Id
    private UUID id;

    @Column("name")
    private String name;

    @Column("email")
    private String email;

    @Column("password")
    private String password;

    @Column("token")
    private String token;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime created;

    @LastModifiedDate
    @Column("modified_at")
    private LocalDateTime modified;

    @Column("last_login")
    private LocalDateTime lastLogin;

    @Column("is_active")
    @Builder.Default
    private boolean isActive = true;
}
