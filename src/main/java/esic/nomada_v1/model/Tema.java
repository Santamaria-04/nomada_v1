package esic.nomada_v1.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import esic.nomada_v1.dto.TemaDTO;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "temas")
public class Tema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tema")
    private Integer idTema;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    // Relación inversa con Recurso
    @OneToMany(mappedBy = "tema", fetch = FetchType.LAZY)
    private List<Recurso> recursos;

    // Constructor DTO → Entity
    public Tema(TemaDTO dto) {
        this.idTema = dto.getIdTema();
        this.nombre = dto.getNombre();
        this.descripcion = dto.getDescripcion();
    }
}
