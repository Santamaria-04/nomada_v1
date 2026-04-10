package esic.nomada_v1.service;


import org.springframework.stereotype.Service;
import esic.nomada_v1.dto.RecursoDTO;
import esic.nomada_v1.model.Recurso;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FabricaRecursoService {

    public Recurso createRecurso(RecursoDTO dto) {
        return new Recurso(dto);
    }

    public RecursoDTO createRecursoDTO(Recurso entidad) {
        return new RecursoDTO(entidad);
    }

    public List<RecursoDTO> crearRecursosDTO(List<Recurso> lista) {
        return lista.stream()
                .map(this::createRecursoDTO)
                .collect(Collectors.toList());
    }
}
