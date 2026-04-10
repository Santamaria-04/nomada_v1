package esic.nomada_v1.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import esic.nomada_v1.model.Historial;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class HistorialDTO {

    private Integer idHistorial;
    private String terminoBusqueda;
    private LocalDateTime fecha;
    private Integer idUsuario;
    private Integer idRecurso;
    private String tituloRecurso;

    public HistorialDTO(Historial entidad) {
        if (entidad != null) {
            this.idHistorial = entidad.getIdHistorial();
            this.terminoBusqueda = entidad.getTerminoBusqueda();
            this.fecha = entidad.getFecha();

            if (entidad.getUsuario() != null) {
                this.idUsuario = entidad.getUsuario().getIdUsuario();
            }

            if (entidad.getRecurso() != null) {
                this.idRecurso = entidad.getRecurso().getIdRecurso();
                this.tituloRecurso = entidad.getRecurso().getTitulo();
            }
        }
    }
}
