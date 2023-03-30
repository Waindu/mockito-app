package org.mycompay.myapp;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.mycompay.myapp.data.Datos;
import org.mycompay.myapp.domain.Examen;
import org.mycompay.myapp.repositories.ExamenRepository;
import org.mycompay.myapp.repositories.ExamenRepositoryImpl;
import org.mycompay.myapp.repositories.PreguntaRepository;
import org.mycompay.myapp.repositories.PreguntaRepositoryImpl;
import org.mycompay.myapp.services.ExamenServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExamenServiceTest {
    @Mock
    ExamenRepository examenRepository;

    @Mock
    PreguntaRepository preguntaRepository;

    @InjectMocks
    ExamenServiceImpl examenService;

    @Test
    void findExamenPorNombre() {
        // given: preparo el contexto, dependencias, parametros, etc
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);

        // when: invocamos el metodo a probar
        Optional<Examen> examen = examenService.findExamenPorNombre("Matemáticas");

        // then: validamos el test
        assertTrue(examen.isPresent());
        assertEquals("Matemáticas", examen.get().getNombre());
    }

    @Test
    void findExamenPorNombreListaVacia() {
        List<Examen> datos = Collections.emptyList();
        when(examenRepository.findAll()).thenReturn(datos);

        Optional<Examen> examen = examenService.findExamenPorNombre("Matemática");

        assertFalse(examen.isPresent());
    }

    @Test
    void findPreguntasExamenVerificarLlamadas() {
        // given
        //  -dependencia tran
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        //  -dependencia
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        // when (dependencia)
        Examen examen = examenService.findExamenPorNombreConPreguntas("Matemáticas");

        // then chequeamos tanto ej del metodo como la dependencia tran
        assertEquals(5, examen.getPreguntas().size());
        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }

    @Test
    void testGuardarExamen() {
        // GIVEN
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);
        when(examenRepository.guardar(any(Examen.class))).then(new Answer<Examen>() {
                Long secuencia = 51L;
                @Override
                public Examen answer(InvocationOnMock invocation) throws Throwable {
                    Examen e = invocation.getArgument(0);
                    e.setId(secuencia++);
                    return e;
                }
        });

        // WHEN
        Examen examen = examenService.guardar(newExamen);

        // THEN
        //  - JUnit5
        assertNotNull(examen.getId());
        assertEquals(51L, examen.getId());
        assertEquals("Física", examen.getNombre());
        //  - Mockito
        verify(examenRepository).guardar(any(Examen.class));
        verify(preguntaRepository).guardarVarias(anyList());
    }

    @Test
    void testNumeroDeInvocaciones() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        examenService.findExamenPorNombreConPreguntas("Matemáticas");

        // THEN: verify
        verify(preguntaRepository).findPreguntasPorExamenId(5L);
        // sobrecargas de verify
        verify(preguntaRepository, times(1)).findPreguntasPorExamenId(5L);
        verify(preguntaRepository, atLeastOnce()).findPreguntasPorExamenId(5L);
        verify(preguntaRepository, atMostOnce()).findPreguntasPorExamenId(5L);
        verify(preguntaRepository, atLeast(1)).findPreguntasPorExamenId(5L);
        verify(preguntaRepository, atMost(100)).findPreguntasPorExamenId(5L);
        verify(examenRepository, never()).guardar(any(Examen.class));
    }

    @Test
    void testManejoException() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES_ID_NULL);
        when(preguntaRepository.findPreguntasPorExamenId((isNull()))).thenThrow(new IllegalArgumentException());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            examenService.findExamenPorNombreConPreguntas("Matemáticas");
        });

        assertEquals(IllegalArgumentException.class, exception.getClass());
        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(isNull());
    }

}
