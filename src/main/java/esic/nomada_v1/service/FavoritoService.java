package esic.nomada_v1.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import esic.nomada_v1.dto.FavoritoDTO;
import esic.nomada_v1.model.Aportacion;
import esic.nomada_v1.model.Favorito;
import esic.nomada_v1.model.Usuario;
import esic.nomada_v1.model.Recurso;
import esic.nomada_v1.repository.AportacionRepository;
import esic.nomada_v1.repository.FavoritoRepository;
import esic.nomada_v1.repository.UsuarioRepository;
import esic.nomada_v1.repository.RecursoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RecursoRepository recursoRepository;
    private final AportacionRepository aportacionRepository;
    private final FabricaFavoritoService fabricaFavorito;

    public FavoritoService(FavoritoRepository favoritoRepository,
                           UsuarioRepository usuarioRepository,
                           RecursoRepository recursoRepository,
                           AportacionRepository aportacionRepository,
                           FabricaFavoritoService fabricaFavorito) {
        this.favoritoRepository = favoritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.recursoRepository = recursoRepository;
        this.aportacionRepository = aportacionRepository;
        this.fabricaFavorito = fabricaFavorito;
    }

    @Transactional
    public FavoritoDTO save(FavoritoDTO dto) {
        if (dto == null || dto.getIdUsuario() == null) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }
        validateTarget(dto);

        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        Favorito favorito = fabricaFavorito.createFavorito(dto);
        favorito.setUsuario(usuario);
        favorito.setFechaGuardado(LocalDateTime.now());

        if (dto.getIdRecurso() != null) {
            if (favoritoRepository.existsByUsuario_IdUsuarioAndRecurso_IdRecurso(
                    dto.getIdUsuario(), dto.getIdRecurso())) {
                throw new IllegalArgumentException("El recurso ya está en favoritos");
            }

            Recurso recurso = recursoRepository.findById(dto.getIdRecurso())
                    .orElseThrow(() -> new NoSuchElementException("Recurso no encontrado"));
            favorito.setRecurso(recurso);
            favorito.setAportacion(null);
        } else {
            if (favoritoRepository.existsByUsuario_IdUsuarioAndAportacion_IdAportacion(
                    dto.getIdUsuario(), dto.getIdAportacion())) {
                throw new IllegalArgumentException("La aportación ya está en favoritos");
            }

            Aportacion aportacion = aportacionRepository.findById(dto.getIdAportacion())
                    .orElseThrow(() -> new NoSuchElementException("Aportación no encontrada"));
            if (aportacion.isEliminada()) {
                throw new IllegalArgumentException("No se puede guardar una aportación eliminada");
            }
            favorito.setAportacion(aportacion);
            favorito.setRecurso(null);
        }

        Favorito guardado = favoritoRepository.save(favorito);

        return fabricaFavorito.createFavoritoDTO(guardado);
    }

    @Transactional(readOnly = true)
    public List<FavoritoDTO> findByUsuario(Integer idUsuario) {
        return favoritoRepository.findByUsuario_IdUsuario(idUsuario)
                .stream()
                .map(fabricaFavorito::createFavoritoDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Integer id, Integer requesterUserId, boolean isAdmin) {
        Favorito favorito = favoritoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Favorito no encontrado"));

        if (!isAdmin && !favorito.getUsuario().getIdUsuario().equals(requesterUserId)) {
            throw new IllegalArgumentException("No puedes eliminar favoritos de otro usuario");
        }

        favoritoRepository.delete(favorito);
    }

    private void validateTarget(FavoritoDTO dto) {
        boolean hasRecurso = dto.getIdRecurso() != null;
        boolean hasAportacion = dto.getIdAportacion() != null;

        if (hasRecurso == hasAportacion) {
            throw new IllegalArgumentException("Debes indicar exactamente un recurso o una aportación");
        }
    }
}
