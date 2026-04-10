package esic.nomada_v1.service;

import org.springframework.stereotype.Service;
import esic.nomada_v1.dto.UsuarioDTO;
import esic.nomada_v1.model.Usuario;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FabricaUsuarioService {

    public Usuario createUsuario(UsuarioDTO dto) {
        return new Usuario(dto);
    }

    public UsuarioDTO createUsuarioDTO(Usuario entidad) {
        return new UsuarioDTO(entidad);
    }

    public List<UsuarioDTO> crearUsuariosDTO(List<Usuario> lista) {
        return lista.stream()
                .map(this::createUsuarioDTO)
                .collect(Collectors.toList());
    }
}
