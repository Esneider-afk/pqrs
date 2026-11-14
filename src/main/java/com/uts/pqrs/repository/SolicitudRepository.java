package com.uts.pqrs.repository;

import com.uts.pqrs.model.Dependencia;
import com.uts.pqrs.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    Optional<Solicitud> findByRadicadoIgnoreCase(String radicado);

    List<Solicitud> findAllByOrderByFechaVencimientoAsc();

    List<Solicitud> findByDependenciaOrderByFechaVencimientoAsc(Dependencia dependencia);

    long countByFechaRadicacionBetween(LocalDate desde, LocalDate hasta);
}