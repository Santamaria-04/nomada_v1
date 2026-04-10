package esic.nomada_v1.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import esic.nomada_v1.model.Usuario;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UsuarioDTO {

    private Integer idUsuario;
    private String nombre;
    private String email;
    private String password; // solo para crear
    private String imagenPerfil;
    private String rol;
    private LocalDateTime fechaRegistro;

    public UsuarioDTO(Usuario entidad) {
        if (entidad != null) {
            this.idUsuario = entidad.getIdUsuario();
            this.nombre = entidad.getNombre();
            this.email = entidad.getEmail();
            this.imagenPerfil = entidad.getImagenPerfil();
            this.rol = entidad.getRol();
            this.fechaRegistro = entidad.getFechaRegistro();
        }
    }
}
