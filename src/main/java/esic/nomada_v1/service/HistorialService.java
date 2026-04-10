package esic.nomada_v1.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import esic.nomada_v1.dto.HistorialDTO;
import esic.nomada_v1.model.Historial;
import esic.nomada_v1.model.Recurso;
import esic.nomada_v1.model.Usuario;
import esic.nomada_v1.repository.HistorialRepository;
import esic.nomada_v1.repository.RecursoRepository;
import esic.nomada_v1.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class HistorialService {

    private final HistorialRepository historialRepository;
    private final UsuarioRepository usuarioRepository;
    private final RecursoRepository recursoRepository;
    private final FabricaHistorialService fabricaHistorial;

    public HistorialService(HistorialRepository historialRepository,
                            UsuarioRepository usuarioRepository,
                            RecursoRepository recursoRepository,
                            FabricaHistorialService fabricaHistorial) {
        this.historialRepository = historialRepository;
        this.usuarioRepository = usuarioRepository;
        this.recursoRepository = recursoRepository;
        this.fabricaHistorial = fabricaHistorial;
    }

    @Transactional
    public HistorialDTO registrarBusqueda(Integer idUsuario, String terminoBusqueda) {
        return registrar(idUsuario, terminoBusqueda, null);
    }

    @Transactional
    public HistorialDTO registrarConsultaRecurso(Integer idUsuario, Integer idRecurso) {
        Recurso recurso = recursoRepository.findById(idRecurso)
                .orElseThrow(() -> new NoSuchElementException("Recurso no encontrado"));
        return registrar(idUsuario, recurso.getTitulo(), recurso);
    }

    @Transactional(readOnly = true)
    public List<HistorialDTO> findByUsuario(Integer idUsuario) {
        return historialRepository.findByUsuario_IdUsuarioOrderByFechaDesc(idUsuario)
                .stream()
                .map(fabricaHistorial::createHistorialDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteById(Integer idHistorial, Integer requesterUserId, boolean isAdmin) {
        Historial historial = historialRepository.findById(idHistorial)
                .orElseThrow(() -> new NoSuchElementException("Entrada de historial no encontrada"));

        if (!isAdmin && !historial.getUsuario().getIdUsuario().equals(requesterUserId)) {
            throw new IllegalArgumentException("No puedes eliminar historial de otro usuario");
        }

        historialRepository.delete(historial);
    }

    @Transactional
    public void deleteAllByUsuario(Integer idUsuario, Integer requesterUserId, boolean isAdmin) {
        if (!isAdmin && !idUsuario.equals(requesterUserId)) {
            throw new IllegalArgumentException("No puedes borrar historial de otro usuario");
        }

        historialRepository.deleteByUsuario_IdUsuario(idUsuario);
    }

    private HistorialDTO registrar(Integer idUsuario, String terminoBusqueda, Recurso recurso) {
        if (terminoBusqueda == null || terminoBusqueda.trim().isEmpty()) {
            throw new IllegalArgumentException("El término de búsqueda es obligatorio");
        }

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        Historial historial = new Historial();
        historial.setUsuario(usuario);
        historial.setRecurso(recurso);
        historial.setTerminoBusqueda(terminoBusqueda.trim());
        historial.setFecha(LocalDateTime.now());

        Historial guardado = historialRepository.save(historial);
        return fabricaHistorial.createHistorialDTO(guardado);
    }
}
