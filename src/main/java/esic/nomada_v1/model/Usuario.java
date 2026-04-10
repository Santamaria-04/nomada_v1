package esic.nomada_v1.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import esic.nomada_v1.dto.UsuarioDTO;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "imagen_perfil")
    private String imagenPerfil;

    @Column(nullable = false)
    private String rol; // USER o ADMIN

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    public Usuario(UsuarioDTO dto) {
        this.idUsuario = dto.getIdUsuario();
        this.nombre = dto.getNombre();
        this.email = dto.getEmail();
        this.password = dto.getPassword();
        this.imagenPerfil = dto.getImagenPerfil();
        this.rol = dto.getRol();
        this.fechaRegistro = dto.getFechaRegistro();
    }
}
