package esic.nomada_v1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BusquedaResponseDTO {

    private String termino;
    private List<RecursoDTO> recursosLocales;
    private List<RecursoDTO> recursosExternos;
    private List<AportacionDTO> aportaciones;
}
