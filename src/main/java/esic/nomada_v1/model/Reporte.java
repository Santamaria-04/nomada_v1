package esic.nomada_v1.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import esic.nomada_v1.dto.ReporteDTO;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "reportes")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reporte")
    private Integer idReporte;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReporte estado;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_reporta", nullable = false)
    private Usuario usuarioReporta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aportacion", nullable = false)
    private Aportacion aportacion;

    public Reporte(ReporteDTO dto) {
        this.idReporte = dto.getIdReporte();
        this.motivo = dto.getMotivo();
        this.estado = dto.getEstado();
        this.fecha = dto.getFecha();
    }
}
