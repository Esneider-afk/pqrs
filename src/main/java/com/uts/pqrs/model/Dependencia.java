package com.uts.pqrs.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "dependencias")
@Getter
@Setter
@NoArgsConstructor
public class Dependencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    /** Palabras clave separadas por coma, usadas por la clasificación asistida. */
    @Column(length = 1000)
    private String palabrasClave;

    public Dependencia(String nombre, String palabrasClave) {
        this.nombre = nombre;
        this.palabrasClave = palabrasClave;
    }
}