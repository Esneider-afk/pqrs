package com.uts.pqrs.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "solicitudes")
@Getter
@Setter
@NoArgsConstructor
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String radicado;

    @Column(nullable = false, length = 10)
    private String codigoConsulta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoSolicitud tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoSolicitud estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Prioridad prioridad;

    @Column(nullable = false, length = 200)
    private String asunto;

    @Column(nullable = false, length = 4000)
    private String descripcion;

    private boolean anonimo;

    @Column(length = 120)
    private String nombreSolicitante;

    @Column(length = 30)
    private String documento;

    @Column(length = 120)
    private String email;

    @Column(length = 30)
    private String telefono;

    @Column(nullable = false)
    private LocalDate fechaRadicacion;

    @Column(nullable = false)
    private LocalDate fechaVencimiento;

    private LocalDate fechaRespuesta;

    @Column(length = 4000)
    private String respuesta;

    /** Trazabilidad de lo que sugirió el clasificador al radicar. */
    @Column(length = 300)
    private String clasificacionSugerida;

    @ManyToOne
    private Dependencia dependencia;

    @ManyToOne
    private Usuario funcionarioAsignado;
}