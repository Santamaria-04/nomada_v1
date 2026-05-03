package esic.nomada_v1.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import esic.nomada_v1.model.Recurso;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class RecursoDTO {

    private Integer idRecurso;
    private String titulo;
    private String urlEnlace;
    private String descripcion;
    private String imagenUrl;
    private Recurso.TipoRecurso tipoRecurso;
    private String fuente;
    private LocalDate fechaPublicacion;
    private Integer idTema;

    // Constructor Entity → DTO
    public RecursoDTO(Recurso entidad) {
        if (entidad != null) {
            this.idRecurso = entidad.getIdRecurso();
            this.titulo = entidad.getTitulo();
            this.urlEnlace = entidad.getUrlEnlace();
            this.descripcion = entidad.getDescripcion();
            this.imagenUrl = entidad.getImagenUrl();
            this.tipoRecurso = entidad.getTipoRecurso();
            this.fuente = entidad.getFuente();
            this.fechaPublicacion = entidad.getFechaPublicacion();

            if (entidad.getTema() != null) {
                this.idTema = entidad.getTema().getIdTema();
            }
        }
    }
}
