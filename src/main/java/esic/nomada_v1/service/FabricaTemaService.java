package esic.nomada_v1.service;


import org.springframework.stereotype.Service;
import esic.nomada_v1.dto.TemaDTO;
import esic.nomada_v1.model.Tema;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FabricaTemaService {

    public Tema createTema(TemaDTO dto) {
        return new Tema(dto);
    }

    public TemaDTO createTemaDTO(Tema entidad) {
        return new TemaDTO(entidad);
    }

    public List<TemaDTO> crearTemasDTO(List<Tema> lista) {
        return lista.stream()
                .map(this::createTemaDTO)
                .collect(Collectors.toList());
    }
}
