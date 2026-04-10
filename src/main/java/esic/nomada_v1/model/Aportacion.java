package esic.nomada_v1.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import esic.nomada_v1.dto.AportacionDTO;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "aportaciones")
public class Aportacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aportacion")
    private Integer idAportacion;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tema")
    private Tema tema;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_recurso")
    private Recurso recurso;

    @Column(nullable = false)
    private boolean reportada;

    @Column(nullable = false)
    private boolean eliminada;

    public Aportacion(AportacionDTO dto) {
        this.idAportacion = dto.getIdAportacion();
        this.contenido = dto.getContenido();
        this.fechaCreacion = dto.getFechaCreacion();
        this.reportada = dto.isReportada();
        this.eliminada = dto.isEliminada();
    }
}
