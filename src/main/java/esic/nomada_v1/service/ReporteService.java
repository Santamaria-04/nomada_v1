package esic.nomada_v1.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import esic.nomada_v1.dto.ReporteDTO;
import esic.nomada_v1.model.Aportacion;
import esic.nomada_v1.model.EstadoReporte;
import esic.nomada_v1.model.Reporte;
import esic.nomada_v1.model.Usuario;
import esic.nomada_v1.repository.AportacionRepository;
import esic.nomada_v1.repository.ReporteRepository;
import esic.nomada_v1.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final UsuarioRepository usuarioRepository;
    private final AportacionRepository aportacionRepository;
    private final FabricaReporteService fabricaReporte;

    public ReporteService(ReporteRepository reporteRepository,
                          UsuarioRepository usuarioRepository,
                          AportacionRepository aportacionRepository,
                          FabricaReporteService fabricaReporte) {
        this.reporteRepository = reporteRepository;
        this.usuarioRepository = usuarioRepository;
        this.aportacionRepository = aportacionRepository;
        this.fabricaReporte = fabricaReporte;
    }

    @Transactional
    public ReporteDTO create(Integer idUsuarioReporta, ReporteDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("El cuerpo de la petición es obligatorio");
        }
        if (dto.getIdAportacion() == null) {
            throw new IllegalArgumentException("Debes indicar la aportación reportada");
        }
        if (dto.getMotivo() == null || dto.getMotivo().trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo del reporte es obligatorio");
        }

        Usuario usuarioReporta = usuarioRepository.findById(idUsuarioReporta)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        Aportacion aportacion = aportacionRepository.findById(dto.getIdAportacion())
                .orElseThrow(() -> new NoSuchElementException("Aportación no encontrada"));

        if (aportacion.isEliminada()) {
            throw new IllegalArgumentException("No se puede reportar una aportación eliminada");
        }
        if (aportacion.getUsuario().getIdUsuario().equals(idUsuarioReporta)) {
            throw new IllegalArgumentException("No puedes reportar tu propia aportación");
        }
        if (reporteRepository.existsByUsuarioReporta_IdUsuarioAndAportacion_IdAportacionAndEstado(
                idUsuarioReporta, dto.getIdAportacion(), EstadoReporte.PENDIENTE)) {
            throw new IllegalArgumentException("Ya has reportado esta aportación");
        }

        Reporte reporte = fabricaReporte.createReporte(dto);
        reporte.setUsuarioReporta(usuarioReporta);
        reporte.setAportacion(aportacion);
        reporte.setMotivo(dto.getMotivo().trim());
        reporte.setEstado(EstadoReporte.PENDIENTE);
        reporte.setFecha(LocalDateTime.now());

        aportacion.setReportada(true);

        Reporte guardado = reporteRepository.save(reporte);
        return fabricaReporte.createReporteDTO(guardado);
    }

    @Transactional(readOnly = true)
    public List<ReporteDTO> findByUsuario(Integer idUsuario) {
        return reporteRepository.findByUsuarioReporta_IdUsuarioOrderByFechaDesc(idUsuario)
                .stream()
                .map(fabricaReporte::createReporteDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReporteDTO> findPendientes() {
        return reporteRepository.findByEstadoOrderByFechaAsc(EstadoReporte.PENDIENTE)
                .stream()
                .map(fabricaReporte::createReporteDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReporteDTO findById(Integer idReporte) {
        Reporte reporte = reporteRepository.findById(idReporte)
                .orElseThrow(() -> new NoSuchElementException("Reporte no encontrado"));
        return fabricaReporte.createReporteDTO(reporte);
    }

    @Transactional
    public ReporteDTO resolver(Integer idReporte, String accion) {
        Reporte reporte = reporteRepository.findById(idReporte)
                .orElseThrow(() -> new NoSuchElementException("Reporte no encontrado"));

        if (reporte.getEstado() == EstadoReporte.REVISADO) {
            throw new IllegalArgumentException("El reporte ya ha sido revisado");
        }

        String normalizedAction = normalizeAction(accion);
        Aportacion aportacion = reporte.getAportacion();

        if ("ELIMINAR".equals(normalizedAction)) {
            aportacion.setEliminada(true);
            aportacion.setReportada(false);
            marcarPendientesComoRevisados(aportacion.getIdAportacion());
        } else {
            reporte.setEstado(EstadoReporte.REVISADO);
            if (!hayOtrosPendientes(aportacion.getIdAportacion(), idReporte)) {
                aportacion.setReportada(false);
            }
        }

        return fabricaReporte.createReporteDTO(reporte);
    }

    private void marcarPendientesComoRevisados(Integer idAportacion) {
        List<Reporte> pendientes = reporteRepository.findByAportacion_IdAportacionAndEstado(idAportacion, EstadoReporte.PENDIENTE);
        for (Reporte pendiente : pendientes) {
            pendiente.setEstado(EstadoReporte.REVISADO);
        }
    }

    private boolean hayOtrosPendientes(Integer idAportacion, Integer reporteActual) {
        List<Reporte> pendientes = reporteRepository.findByAportacion_IdAportacionAndEstado(idAportacion, EstadoReporte.PENDIENTE);
        return pendientes.stream().anyMatch(reporte -> !reporte.getIdReporte().equals(reporteActual));
    }

    private String normalizeAction(String accion) {
        if (accion == null || accion.trim().isEmpty()) {
            throw new IllegalArgumentException("Debes indicar una acción de moderación");
        }

        String normalizedAction = accion.trim().toUpperCase(Locale.ROOT);
        if (!normalizedAction.equals("MANTENER") && !normalizedAction.equals("ELIMINAR")) {
            throw new IllegalArgumentException("La acción debe ser MANTENER o ELIMINAR");
        }
        return normalizedAction;
    }
}
