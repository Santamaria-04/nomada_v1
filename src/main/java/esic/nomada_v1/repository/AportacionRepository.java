package esic.nomada_v1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import esic.nomada_v1.model.Aportacion;

import java.util.List;

@Repository
public interface AportacionRepository extends JpaRepository<Aportacion, Integer> {

    List<Aportacion> findByUsuario_IdUsuarioAndEliminadaFalse(Integer idUsuario);

    List<Aportacion> findByTema_IdTemaAndEliminadaFalse(Integer idTema);

    List<Aportacion> findByRecurso_IdRecursoAndEliminadaFalse(Integer idRecurso);

    List<Aportacion> findByContenidoContainingIgnoreCaseAndEliminadaFalse(String contenido);

    List<Aportacion> findByEliminadaFalse();

    List<Aportacion> findByReportadaTrueAndEliminadaFalse();
}
