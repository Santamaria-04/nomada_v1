package esic.nomada_v1.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import esic.nomada_v1.dto.RecursoDTO;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "recursos")
public class Recurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recurso")
    private Integer idRecurso;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(name = "url_enlace", nullable = false)
    private String urlEnlace;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_recurso", nullable = false)
    private TipoRecurso tipoRecurso;

    @Column(nullable = false, length = 120)
    private String fuente;

    @Column(name = "fecha_publicacion")
    private LocalDate fechaPublicacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tema")
    private Tema tema;

    // Constructor para convertir DTO → Entity
    public Recurso(RecursoDTO dto) {
        this.idRecurso = dto.getIdRecurso();
        this.titulo = dto.getTitulo();
        this.urlEnlace = dto.getUrlEnlace();
        this.descripcion = dto.getDescripcion();
        this.tipoRecurso = dto.getTipoRecurso();
        this.fuente = dto.getFuente();
        this.fechaPublicacion = dto.getFechaPublicacion();
    }

    public enum TipoRecurso {
        ARTICULO,
        VIDEO,
        PODCAST,
        LIBRO
    }
}
