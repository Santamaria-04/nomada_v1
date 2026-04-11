package esic.nomada_v1.external;

import esic.nomada_v1.dto.RecursoDTO;
import esic.nomada_v1.model.Recurso;

import java.util.List;

public interface ExternalResourceProvider {

    Recurso.TipoRecurso getTipoRecurso();

    List<RecursoDTO> search(String termino, int limit);
}
