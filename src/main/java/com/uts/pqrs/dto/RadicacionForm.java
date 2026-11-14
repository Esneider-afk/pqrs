package com.uts.pqrs.dto;

import com.uts.pqrs.model.TipoSolicitud;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RadicacionForm {

    @NotNull(message = "Seleccione el tipo de solicitud")
    private TipoSolicitud tipo;

    @NotBlank(message = "El asunto es obligatorio")
    @Size(max = 200, message = "El asunto no puede superar 200 caracteres")
    private String asunto;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 20, max = 4000, message = "La descripción debe tener entre 20 y 4000 caracteres")
    private String descripcion;

    /** Solo aplica a denuncias. */
    private boolean anonimo;

    @Size(max = 120)
    private String nombre;

    @Size(max = 30)
    private String documento;

    @Email(message = "Correo electrónico no válido")
    @Size(max = 120)
    private String email;

    @Size(max = 30)
    private String telefono;

    @AssertTrue(message = "Debe aceptar el tratamiento de datos personales")
    private boolean aceptaTratamiento;
}