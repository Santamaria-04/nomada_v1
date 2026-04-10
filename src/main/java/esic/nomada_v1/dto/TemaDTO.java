package esic.nomada_v1.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import esic.nomada_v1.model.Tema;

@Data
@NoArgsConstructor
public class TemaDTO {

    private Integer idTema;
    private String nombre;
    private String descripcion;

    // Constructor Entity → DTO
    public TemaDTO(Tema entidad) {
        if (entidad != null) {
            this.idTema = entidad.getIdTema();
            this.nombre = entidad.getNombre();
            this.descripcion = entidad.getDescripcion();
        }
    }
}
