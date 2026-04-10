package esic.nomada_v1.service;

import org.springframework.stereotype.Service;
import esic.nomada_v1.dto.HistorialDTO;
import esic.nomada_v1.model.Historial;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FabricaHistorialService {

    public Historial createHistorial(HistorialDTO dto) {
        return new Historial(dto);
    }

    public HistorialDTO createHistorialDTO(Historial entidad) {
        return new HistorialDTO(entidad);
    }

    public List<HistorialDTO> crearHistorialDTO(List<Historial> lista) {
        return lista.stream()
                .map(this::createHistorialDTO)
                .collect(Collectors.toList());
    }
}
