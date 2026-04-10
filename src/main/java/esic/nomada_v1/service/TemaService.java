package esic.nomada_v1.service;


import org.springframework.stereotype.Service;
import esic.nomada_v1.dto.TemaDTO;
import esic.nomada_v1.model.Tema;
import esic.nomada_v1.repository.TemaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TemaService {

    private final TemaRepository temaRepository;
    private final FabricaTemaService fabricaTema;

    public TemaService(TemaRepository temaRepository,
                       FabricaTemaService fabricaTema) {
        this.temaRepository = temaRepository;
        this.fabricaTema = fabricaTema;
    }

    public TemaDTO save(TemaDTO dto) {
        Tema entidad = fabricaTema.createTema(dto);
        Tema guardado = temaRepository.save(entidad);
        return fabricaTema.createTemaDTO(guardado);
    }

    public List<TemaDTO> findAll() {
        return temaRepository.findAll()
                .stream()
                .map(fabricaTema::createTemaDTO)
                .collect(Collectors.toList());
    }

    public TemaDTO findById(Integer id) {
        Tema tema = temaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tema no encontrado"));

        return fabricaTema.createTemaDTO(tema);
    }

    public void deleteById(Integer id) {
        if (!temaRepository.existsById(id)) {
            throw new RuntimeException("Tema no encontrado");
        }
        temaRepository.deleteById(id);
    }
}
