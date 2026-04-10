package esic.nomada_v1.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import esic.nomada_v1.model.Favorito;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class FavoritoDTO {

    private Integer idFavorito;
    private Integer idUsuario;
    private Integer idRecurso;
    private Integer idAportacion;
    private LocalDateTime fechaGuardado;
    private String tipoFavorito;
    private String tituloElemento;

    public FavoritoDTO(Favorito entidad) {
        if (entidad != null) {
            this.idFavorito = entidad.getIdFavorito();
            this.fechaGuardado = entidad.getFechaGuardado();

            if (entidad.getUsuario() != null) {
                this.idUsuario = entidad.getUsuario().getIdUsuario();
            }

            if (entidad.getRecurso() != null) {
                this.idRecurso = entidad.getRecurso().getIdRecurso();
                this.tipoFavorito = "RECURSO";
                this.tituloElemento = entidad.getRecurso().getTitulo();
            }

            if (entidad.getAportacion() != null) {
                this.idAportacion = entidad.getAportacion().getIdAportacion();
                this.tipoFavorito = "APORTACION";
                this.tituloElemento = entidad.getAportacion().getContenido();
            }
        }
    }
}
