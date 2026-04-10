package esic.nomada_v1.service;

import org.springframework.stereotype.Service;
import esic.nomada_v1.dto.AportacionDTO;
import esic.nomada_v1.model.Aportacion;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FabricaAportacionService {

    public Aportacion createAportacion(AportacionDTO dto) {
        return new Aportacion(dto);
    }

    public AportacionDTO createAportacionDTO(Aportacion entidad) {
        return new AportacionDTO(entidad);
    }

    public List<AportacionDTO> crearAportacionesDTO(List<Aportacion> lista) {
        return lista.stream()
                .map(this::createAportacionDTO)
                .collect(Collectors.toList());
    }
}
