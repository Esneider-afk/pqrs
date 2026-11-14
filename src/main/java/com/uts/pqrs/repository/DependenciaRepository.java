package com.uts.pqrs.repository;

import com.uts.pqrs.model.Dependencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DependenciaRepository extends JpaRepository<Dependencia, Long> {
    Optional<Dependencia> findByNombre(String nombre);
}