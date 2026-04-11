package esic.nomada_v1.service;

import org.junit.jupiter.api.Test;
import esic.nomada_v1.dto.FavoritoDTO;
import esic.nomada_v1.dto.RecursoDTO;
import esic.nomada_v1.model.Favorito;
import esic.nomada_v1.model.Recurso;
import esic.nomada_v1.model.Usuario;
import esic.nomada_v1.repository.AportacionRepository;
import esic.nomada_v1.repository.FavoritoRepository;
import esic.nomada_v1.repository.RecursoRepository;
import esic.nomada_v1.repository.TemaRepository;
import esic.nomada_v1.repository.UsuarioRepository;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FavoritoServiceTest {

    @Test
    void shouldCreateLocalResourceWhenSavingExternalResourceAsFavorite() {
        TestRepositories repositories = new TestRepositories();
        repositories.usuario = createUsuario(7);
        FavoritoService service = repositories.createService();

        FavoritoDTO result = service.save(createExternalFavoriteDto());

        assertEquals(21, result.getIdFavorito());
        assertEquals(33, result.getIdRecurso());
        assertEquals("RECURSO", result.getTipoFavorito());
        assertEquals("Video Java", result.getTituloElemento());
        assertEquals(1, repositories.recursoSaveCalls);
        assertEquals("Video Java", repositories.savedResource.getTitulo());
        assertEquals("https://www.youtube.com/watch?v=abc", repositories.savedResource.getUrlEnlace());
        assertEquals(Recurso.TipoRecurso.VIDEO, repositories.savedResource.getTipoRecurso());
        assertEquals("YouTube", repositories.savedResource.getFuente());
    }

    @Test
    void shouldReuseExistingResourceWhenExternalUrlAlreadyExists() {
        TestRepositories repositories = new TestRepositories();
        repositories.usuario = createUsuario(7);
        repositories.existingResource = createExistingResource();
        FavoritoService service = repositories.createService();

        FavoritoDTO result = service.save(createExternalFavoriteDto());

        assertEquals(33, result.getIdRecurso());
        assertEquals(0, repositories.recursoSaveCalls);
    }

    private FavoritoDTO createExternalFavoriteDto() {
        RecursoDTO recursoExterno = new RecursoDTO();
        recursoExterno.setTitulo(" Video Java ");
        recursoExterno.setUrlEnlace(" https://www.youtube.com/watch?v=abc ");
        recursoExterno.setDescripcion("Video educativo");
        recursoExterno.setTipoRecurso(Recurso.TipoRecurso.VIDEO);
        recursoExterno.setFuente(" YouTube ");

        FavoritoDTO dto = new FavoritoDTO();
        dto.setIdUsuario(7);
        dto.setRecursoExterno(recursoExterno);
        return dto;
    }

    private Usuario createUsuario(Integer idUsuario) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);
        usuario.setEmail("pablo@example.com");
        usuario.setNombre("Pablo");
        usuario.setRol("USER");
        return usuario;
    }

    private Recurso createExistingResource() {
        Recurso recurso = new Recurso();
        recurso.setIdRecurso(33);
        recurso.setTitulo("Video Java");
        recurso.setUrlEnlace("https://www.youtube.com/watch?v=abc");
        recurso.setTipoRecurso(Recurso.TipoRecurso.VIDEO);
        recurso.setFuente("YouTube");
        return recurso;
    }

    private static class TestRepositories {

        private Usuario usuario;
        private Recurso existingResource;
        private Recurso savedResource;
        private int recursoSaveCalls;

        private FavoritoService createService() {
            return new FavoritoService(
                    favoritoRepository(),
                    usuarioRepository(),
                    recursoRepository(),
                    emptyRepository(AportacionRepository.class),
                    emptyRepository(TemaRepository.class),
                    new FabricaFavoritoService()
            );
        }

        private FavoritoRepository favoritoRepository() {
            return proxy(FavoritoRepository.class, (proxy, method, args) -> {
                if ("existsByUsuario_IdUsuarioAndRecurso_IdRecurso".equals(method.getName())) {
                    return false;
                }
                if ("save".equals(method.getName())) {
                    Favorito favorito = (Favorito) args[0];
                    favorito.setIdFavorito(21);
                    return favorito;
                }
                throw unsupported(method.getName());
            });
        }

        private UsuarioRepository usuarioRepository() {
            return proxy(UsuarioRepository.class, (proxy, method, args) -> {
                if ("findById".equals(method.getName())) {
                    return Optional.of(usuario);
                }
                throw unsupported(method.getName());
            });
        }

        private RecursoRepository recursoRepository() {
            return proxy(RecursoRepository.class, (proxy, method, args) -> {
                if ("findByUrlEnlace".equals(method.getName())) {
                    return Optional.ofNullable(existingResource);
                }
                if ("save".equals(method.getName())) {
                    recursoSaveCalls++;
                    savedResource = (Recurso) args[0];
                    savedResource.setIdRecurso(33);
                    return savedResource;
                }
                throw unsupported(method.getName());
            });
        }

        private RuntimeException unsupported(String methodName) {
            return new UnsupportedOperationException("Método no esperado en test: " + methodName);
        }

        @SuppressWarnings("unchecked")
        private <T> T emptyRepository(Class<T> repositoryType) {
            return proxy(repositoryType, (proxy, method, args) -> {
                throw unsupported(method.getName());
            });
        }

        @SuppressWarnings("unchecked")
        private <T> T proxy(Class<T> repositoryType, InvocationHandler handler) {
            return (T) Proxy.newProxyInstance(
                    repositoryType.getClassLoader(),
                    new Class<?>[]{repositoryType},
                    handler
            );
        }
    }
}
