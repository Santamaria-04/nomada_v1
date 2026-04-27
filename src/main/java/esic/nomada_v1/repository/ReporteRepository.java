package esic.nomada_v1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import esic.nomada_v1.model.EstadoReporte;
import esic.nomada_v1.model.Reporte;

import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Integer> {

    List<Reporte> findByUsuarioReporta_IdUsuarioOrderByFechaDesc(Integer idUsuario);

    List<Reporte> findByEstadoOrderByFechaAsc(EstadoReporte estado);

    boolean existsByUsuarioReporta_IdUsuarioAndAportacion_IdAportacionAndEstado(Integer idUsuario,
                                                                                Integer idAportacion,
                                                                                EstadoReporte estado);

    List<Reporte> findByAportacion_IdAportacionAndEstado(Integer idAportacion, EstadoReporte estado);

    boolean existsByUsuarioReporta_IdUsuario(Integer idUsuario);

    boolean existsByAportacion_IdAportacion(Integer idAportacion);
}
