package esic.nomada_v1.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import esic.nomada_v1.model.Tema;

@Repository
public interface TemaRepository extends JpaRepository<Tema, Integer> {
}
