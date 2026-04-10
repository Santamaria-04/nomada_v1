package esic.nomada_v1.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import esic.nomada_v1.model.Aportacion;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AportacionDTO {

    private Integer idAportacion;
    private String contenido;
    private LocalDateTime fechaCreacion;
    private Integer idUsuario;
    private Integer idTema;
    private Integer idRecurso;
    private String nombreUsuario;
    private boolean reportada;
    private boolean eliminada;

    public AportacionDTO(Aportacion entidad) {
        if (entidad != null) {
            this.idAportacion = entidad.getIdAportacion();
            this.contenido = entidad.getContenido();
            this.fechaCreacion = entidad.getFechaCreacion();
            this.reportada = entidad.isReportada();
            this.eliminada = entidad.isEliminada();

            if (entidad.getUsuario() != null) {
                this.idUsuario = entidad.getUsuario().getIdUsuario();
                this.nombreUsuario = entidad.getUsuario().getNombre();
            }

            if (entidad.getTema() != null) {
                this.idTema = entidad.getTema().getIdTema();
            }

            if (entidad.getRecurso() != null) {
                this.idRecurso = entidad.getRecurso().getIdRecurso();
            }
        }
    }
}
