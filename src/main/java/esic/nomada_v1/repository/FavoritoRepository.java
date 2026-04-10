package esic.nomada_v1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import esic.nomada_v1.model.Favorito;

import java.util.List;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Integer> {

    List<Favorito> findByUsuario_IdUsuario(Integer idUsuario);

    boolean existsByUsuario_IdUsuarioAndRecurso_IdRecurso(Integer idUsuario, Integer idRecurso);

    boolean existsByUsuario_IdUsuarioAndAportacion_IdAportacion(Integer idUsuario, Integer idAportacion);
}
