package esic.nomada_v1.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import esic.nomada_v1.dto.HistorialDTO;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "historial")
public class Historial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Integer idHistorial;

    @Column(name = "termino_busqueda", nullable = false)
    private String terminoBusqueda;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_recurso")
    private Recurso recurso;

    public Historial(HistorialDTO dto) {
        this.idHistorial = dto.getIdHistorial();
        this.terminoBusqueda = dto.getTerminoBusqueda();
        this.fecha = dto.getFecha();
    }
}
