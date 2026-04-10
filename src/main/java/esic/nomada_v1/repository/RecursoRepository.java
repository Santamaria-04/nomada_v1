package esic.nomada_v1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import esic.nomada_v1.model.Recurso;

import java.util.List;

@Repository
public interface RecursoRepository extends JpaRepository<Recurso, Integer> {

    List<Recurso> findByTituloContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String titulo,
                                                                                    String descripcion);
}
