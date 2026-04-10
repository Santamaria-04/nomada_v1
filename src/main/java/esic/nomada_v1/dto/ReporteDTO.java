package esic.nomada_v1.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import esic.nomada_v1.model.Reporte;
import esic.nomada_v1.model.EstadoReporte;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ReporteDTO {

    private Integer idReporte;
    private String motivo;
    private EstadoReporte estado;
    private LocalDateTime fecha;
    private Integer idUsuarioReporta;
    private Integer idAportacion;
    private String nombreUsuarioReporta;
    private String contenidoAportacion;

    public ReporteDTO(Reporte entidad) {
        if (entidad != null) {
            this.idReporte = entidad.getIdReporte();
            this.motivo = entidad.getMotivo();
            this.estado = entidad.getEstado();
            this.fecha = entidad.getFecha();

            if (entidad.getUsuarioReporta() != null) {
                this.idUsuarioReporta = entidad.getUsuarioReporta().getIdUsuario();
                this.nombreUsuarioReporta = entidad.getUsuarioReporta().getNombre();
            }

            if (entidad.getAportacion() != null) {
                this.idAportacion = entidad.getAportacion().getIdAportacion();
                this.contenidoAportacion = entidad.getAportacion().getContenido();
            }
        }
    }
}
