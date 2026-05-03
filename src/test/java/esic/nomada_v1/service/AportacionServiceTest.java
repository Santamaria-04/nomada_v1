package esic.nomada_v1.service;

import esic.nomada_v1.dto.AportacionDTO;
import esic.nomada_v1.repository.AportacionRepository;
import esic.nomada_v1.repository.RecursoRepository;
import esic.nomada_v1.repository.TemaRepository;
import esic.nomada_v1.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AportacionServiceTest {

    @Test
    void shouldRejectContributionWithoutTemaOrRecurso() {
        AportacionService service = createService();

        AportacionDTO dto = new AportacionDTO();
        dto.setIdUsuario(1);
        dto.setContenido("Texto valido");

        assertThrows(IllegalArgumentException.class, () -> service.save(dto));
    }

    @Test
    void shouldRejectContributionWithTemaAndRecursoAtTheSameTime() {
        AportacionService service = createService();

        AportacionDTO dto = new AportacionDTO();
        dto.setIdUsuario(1);
        dto.setContenido("Texto valido");
        dto.setIdTema(10);
        dto.setIdRecurso(20);

        assertThrows(IllegalArgumentException.class, () -> service.save(dto));
    }

    private AportacionService createService() {
        return new AportacionService(
                emptyRepository(AportacionRepository.class),
                emptyRepository(UsuarioRepository.class),
                emptyRepository(TemaRepository.class),
                emptyRepository(RecursoRepository.class),
                new FabricaAportacionService()
        );
    }

    @SuppressWarnings("unchecked")
    private <T> T emptyRepository(Class<T> repositoryType) {
        InvocationHandler handler = (proxy, method, args) -> {
            throw new UnsupportedOperationException("Metodo no esperado en test: " + method.getName());
        };
        return (T) Proxy.newProxyInstance(
                repositoryType.getClassLoader(),
                new Class<?>[]{repositoryType},
                handler
        );
    }
}
