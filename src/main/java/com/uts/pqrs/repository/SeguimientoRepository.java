package com.uts.pqrs.repository;

import com.uts.pqrs.model.Seguimiento;
import com.uts.pqrs.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeguimientoRepository extends JpaRepository<Seguimiento, Long> {
    List<Seguimiento> findBySolicitudOrderByFechaAsc(Solicitud solicitud);
}