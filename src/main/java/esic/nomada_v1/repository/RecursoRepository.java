package esic.nomada_v1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import esic.nomada_v1.model.Recurso;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecursoRepository extends JpaRepository<Recurso, Integer> {

    List<Recurso> findByTituloContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String titulo,
                                                                                    String descripcion);

    List<Recurso> findByTema_IdTema(Integer idTema);

    Optional<Recurso> findByUrlEnlace(String urlEnlace);

    boolean existsByTema_IdTema(Integer idTema);
}
