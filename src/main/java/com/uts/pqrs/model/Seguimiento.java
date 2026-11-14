package com.uts.pqrs.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "seguimientos")
@Getter
@Setter
@NoArgsConstructor
public class Seguimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Solicitud solicitud;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(length = 120)
    private String autor;

    @Column(nullable = false, length = 100)
    private String accion;

    @Column(length = 2000)
    private String detalle;
}