package esic.nomada_v1.service;

import org.springframework.stereotype.Service;
import esic.nomada_v1.dto.ReporteDTO;
import esic.nomada_v1.model.Reporte;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FabricaReporteService {

    public Reporte createReporte(ReporteDTO dto) {
        return new Reporte(dto);
    }

    public ReporteDTO createReporteDTO(Reporte entidad) {
        return new ReporteDTO(entidad);
    }

    public List<ReporteDTO> crearReportesDTO(List<Reporte> lista) {
        return lista.stream()
                .map(this::createReporteDTO)
                .collect(Collectors.toList());
    }
}
