package esic.nomada_v1.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EstadoReporteTest {

    @Test
    void shouldContainDefinedWorkflowStates() {
        assertEquals(EstadoReporte.PENDIENTE, EstadoReporte.valueOf("PENDIENTE"));
        assertEquals(EstadoReporte.REVISADO, EstadoReporte.valueOf("REVISADO"));
    }
}
