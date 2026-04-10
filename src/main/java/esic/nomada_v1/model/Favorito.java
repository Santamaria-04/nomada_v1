package esic.nomada_v1.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import esic.nomada_v1.dto.FavoritoDTO;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "favoritos")
public class Favorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_favorito")
    private Integer idFavorito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_recurso")
    private Recurso recurso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aportacion")
    private Aportacion aportacion;

    @Column(name = "fecha_guardado")
    private LocalDateTime fechaGuardado;

    public Favorito(FavoritoDTO dto) {
        this.idFavorito = dto.getIdFavorito();
        this.fechaGuardado = dto.getFechaGuardado();
    }
}
