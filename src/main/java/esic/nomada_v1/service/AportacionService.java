package esic.nomada_v1.service;

import org.springframework.stereotype.Service;
import esic.nomada_v1.dto.AportacionDTO;
import esic.nomada_v1.model.Aportacion;
import esic.nomada_v1.model.Recurso;
import esic.nomada_v1.model.Tema;
import esic.nomada_v1.model.Usuario;
import esic.nomada_v1.repository.AportacionRepository;
import esic.nomada_v1.repository.RecursoRepository;
import esic.nomada_v1.repository.TemaRepository;
import esic.nomada_v1.repository.UsuarioRepository;
import esic.nomada_v1.util.TextValidationUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class AportacionService {

    private final AportacionRepository aportacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final TemaRepository temaRepository;
    private final RecursoRepository recursoRepository;
    private final FabricaAportacionService fabricaAportacion;

    public AportacionService(AportacionRepository aportacionRepository,
                             UsuarioRepository usuarioRepository,
                             TemaRepository temaRepository,
                             RecursoRepository recursoRepository,
                             FabricaAportacionService fabricaAportacion) {
        this.aportacionRepository = aportacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.temaRepository = temaRepository;
        this.recursoRepository = recursoRepository;
        this.fabricaAportacion = fabricaAportacion;
    }

    @Transactional
    public AportacionDTO save(AportacionDTO dto) {
        validateDto(dto);

        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        Tema tema = null;
        if (dto.getIdTema() != null) {
            tema = temaRepository.findById(dto.getIdTema())
                    .orElseThrow(() -> new NoSuchElementException("Tema no encontrado"));
        }

        Recurso recurso = null;
        if (dto.getIdRecurso() != null) {
            recurso = recursoRepository.findById(dto.getIdRecurso())
                    .orElseThrow(() -> new NoSuchElementException("Recurso no encontrado"));
        }

        Aportacion aportacion = fabricaAportacion.createAportacion(dto);
        aportacion.setContenido(dto.getContenido().trim());
        aportacion.setUsuario(usuario);
        aportacion.setTema(tema);
        aportacion.setRecurso(recurso);
        aportacion.setFechaCreacion(LocalDateTime.now());
        aportacion.setReportada(false);
        aportacion.setEliminada(false);

        Aportacion guardada = aportacionRepository.save(aportacion);
        return fabricaAportacion.createAportacionDTO(guardada);
    }

    @Transactional
    public AportacionDTO update(Integer id, AportacionDTO dto) {
        validateDto(dto);

        Aportacion aportacion = aportacionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Aportación no encontrada"));

        if (!aportacion.getUsuario().getIdUsuario().equals(dto.getIdUsuario())) {
            throw new IllegalArgumentException("Solo el autor puede editar la aportación");
        }
        if (aportacion.isEliminada()) {
            throw new IllegalArgumentException("No se puede editar una aportación eliminada");
        }

        Tema tema = null;
        if (dto.getIdTema() != null) {
            tema = temaRepository.findById(dto.getIdTema())
                    .orElseThrow(() -> new NoSuchElementException("Tema no encontrado"));
        }

        Recurso recurso = null;
        if (dto.getIdRecurso() != null) {
            recurso = recursoRepository.findById(dto.getIdRecurso())
                    .orElseThrow(() -> new NoSuchElementException("Recurso no encontrado"));
        }

        aportacion.setContenido(dto.getContenido().trim());
        aportacion.setTema(tema);
        aportacion.setRecurso(recurso);

        Aportacion guardada = aportacionRepository.save(aportacion);
        return fabricaAportacion.createAportacionDTO(guardada);
    }

    @Transactional(readOnly = true)
    public List<AportacionDTO> findAll() {
        return aportacionRepository.findByEliminadaFalse()
                .stream()
                .map(fabricaAportacion::createAportacionDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AportacionDTO findById(Integer id) {
        Aportacion aportacion = aportacionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Aportación no encontrada"));
        if (aportacion.isEliminada()) {
            throw new NoSuchElementException("Aportación no encontrada");
        }
        return fabricaAportacion.createAportacionDTO(aportacion);
    }

    @Transactional(readOnly = true)
    public List<AportacionDTO> findByUsuario(Integer idUsuario) {
        return aportacionRepository.findByUsuario_IdUsuarioAndEliminadaFalse(idUsuario)
                .stream()
                .map(fabricaAportacion::createAportacionDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AportacionDTO> findByTema(Integer idTema) {
        return aportacionRepository.findByTema_IdTemaAndEliminadaFalse(idTema)
                .stream()
                .map(fabricaAportacion::createAportacionDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AportacionDTO> findByRecurso(Integer idRecurso) {
        return aportacionRepository.findByRecurso_IdRecursoAndEliminadaFalse(idRecurso)
                .stream()
                .map(fabricaAportacion::createAportacionDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Integer id, Integer idUsuario, boolean isAdmin) {
        Aportacion aportacion = aportacionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Aportación no encontrada"));

        if (!isAdmin && !aportacion.getUsuario().getIdUsuario().equals(idUsuario)) {
            throw new IllegalArgumentException("Solo el autor puede eliminar la aportación");
        }

        aportacion.setEliminada(true);
        aportacion.setReportada(false);
    }

    @Transactional(readOnly = true)
    public List<AportacionDTO> findReportadas() {
        return aportacionRepository.findByReportadaTrueAndEliminadaFalse()
                .stream()
                .map(fabricaAportacion::createAportacionDTO)
                .collect(Collectors.toList());
    }

    private void validateDto(AportacionDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("El cuerpo de la petición es obligatorio");
        }
        if (dto.getIdUsuario() == null) {
            throw new IllegalArgumentException("La aportación debe incluir un usuario");
        }
        boolean hasTema = dto.getIdTema() != null;
        boolean hasRecurso = dto.getIdRecurso() != null;
        if (hasTema == hasRecurso) {
            throw new IllegalArgumentException("La aportación debe estar asociada exactamente a un tema o a un recurso");
        }
        if (dto.getContenido() == null || dto.getContenido().trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido de la aportación es obligatorio");
        }
        if (TextValidationUtils.containsExternalLink(dto.getContenido())) {
            throw new IllegalArgumentException("No se permiten enlaces externos en las aportaciones");
        }
    }
}
