package esic.nomada_v1.service;


import org.springframework.stereotype.Service;
import esic.nomada_v1.dto.FavoritoDTO;
import esic.nomada_v1.model.Favorito;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FabricaFavoritoService {

    public Favorito createFavorito(FavoritoDTO dto) {
        return new Favorito(dto);
    }

    public FavoritoDTO createFavoritoDTO(Favorito entidad) {
        return new FavoritoDTO(entidad);
    }

    public List<FavoritoDTO> crearFavoritosDTO(List<Favorito> lista) {
        return lista.stream()
                .map(this::createFavoritoDTO)
                .collect(Collectors.toList());
    }
}
