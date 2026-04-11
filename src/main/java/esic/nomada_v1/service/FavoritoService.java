package esic.nomada_v1.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import esic.nomada_v1.dto.FavoritoDTO;
import esic.nomada_v1.dto.RecursoDTO;
import esic.nomada_v1.model.Aportacion;
import esic.nomada_v1.model.Favorito;
import esic.nomada_v1.model.Usuario;
import esic.nomada_v1.model.Recurso;
import esic.nomada_v1.model.Tema;
import esic.nomada_v1.repository.AportacionRepository;
import esic.nomada_v1.repository.FavoritoRepository;
import esic.nomada_v1.repository.UsuarioRepository;
import esic.nomada_v1.repository.RecursoRepository;
import esic.nomada_v1.repository.TemaRepository;

import java.time.LocalDate;
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
    private final TemaRepository temaRepository;
    private final FabricaFavoritoService fabricaFavorito;

    public FavoritoService(FavoritoRepository favoritoRepository,
                           UsuarioRepository usuarioRepository,
                           RecursoRepository recursoRepository,
                           AportacionRepository aportacionRepository,
                           TemaRepository temaRepository,
                           FabricaFavoritoService fabricaFavorito) {
        this.favoritoRepository = favoritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.recursoRepository = recursoRepository;
        this.aportacionRepository = aportacionRepository;
        this.temaRepository = temaRepository;
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
        } else if (dto.getIdAportacion() != null) {
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
        } else {
            Recurso recurso = findOrCreateExternalResource(dto.getRecursoExterno());

            if (favoritoRepository.existsByUsuario_IdUsuarioAndRecurso_IdRecurso(
                    dto.getIdUsuario(), recurso.getIdRecurso())) {
                throw new IllegalArgumentException("El recurso ya está en favoritos");
            }

            favorito.setRecurso(recurso);
            favorito.setAportacion(null);
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
        boolean hasRecursoExterno = dto.getRecursoExterno() != null;

        int targets = 0;
        if (hasRecurso) {
            targets++;
        }
        if (hasAportacion) {
            targets++;
        }
        if (hasRecursoExterno) {
            targets++;
        }

        if (targets != 1) {
            throw new IllegalArgumentException("Debes indicar exactamente un recurso, una aportación o un recurso externo");
        }
    }

    private Recurso findOrCreateExternalResource(RecursoDTO dto) {
        validateExternalResource(dto);

        String urlEnlace = dto.getUrlEnlace().trim();
        return recursoRepository.findByUrlEnlace(urlEnlace)
                .orElseGet(() -> createExternalResource(dto, urlEnlace));
    }

    private Recurso createExternalResource(RecursoDTO dto, String urlEnlace) {
        Recurso recurso = new Recurso();
        recurso.setTitulo(dto.getTitulo().trim());
        recurso.setUrlEnlace(urlEnlace);
        recurso.setDescripcion(dto.getDescripcion());
        recurso.setTipoRecurso(dto.getTipoRecurso());
        recurso.setFuente(dto.getFuente().trim());
        recurso.setFechaPublicacion(dto.getFechaPublicacion());

        if (dto.getIdTema() != null) {
            Tema tema = temaRepository.findById(dto.getIdTema())
                    .orElseThrow(() -> new NoSuchElementException("Tema no encontrado"));
            recurso.setTema(tema);
        }

        return recursoRepository.save(recurso);
    }

    private void validateExternalResource(RecursoDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Los datos del recurso externo son obligatorios");
        }
        if (isBlank(dto.getTitulo())) {
            throw new IllegalArgumentException("El título del recurso externo es obligatorio");
        }
        if (isBlank(dto.getUrlEnlace())) {
            throw new IllegalArgumentException("La URL del recurso externo es obligatoria");
        }
        if (dto.getTipoRecurso() == null) {
            throw new IllegalArgumentException("El tipo del recurso externo es obligatorio");
        }
        if (isBlank(dto.getFuente())) {
            throw new IllegalArgumentException("La fuente del recurso externo es obligatoria");
        }
        if (dto.getFechaPublicacion() != null && dto.getFechaPublicacion().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de publicación del recurso externo no puede ser futura");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
