package esic.nomada_v1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import esic.nomada_v1.model.Historial;

import java.util.List;

@Repository
public interface HistorialRepository extends JpaRepository<Historial, Integer> {

    List<Historial> findByUsuario_IdUsuarioOrderByFechaDesc(Integer idUsuario);

    void deleteByUsuario_IdUsuario(Integer idUsuario);
}
