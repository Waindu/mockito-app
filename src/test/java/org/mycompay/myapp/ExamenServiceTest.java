package org.mycompay.myapp;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
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
import org.mycompay.myapp.services.ExamenService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // no hace falta el mokitoAnnotations
public class ExamenServiceTest {

    @Mock
    ExamenRepository examenRepository;

    @Mock
    PreguntaRepository preguntaRepository;

    @InjectMocks
    ExamenServiceImpl examenService;
    // ExamenService examenService;

    @BeforeEach
    void setUp(){
         // MockitoAnnotations.openMocks(this); se habilita el uso de anotaciones

         // examenRepository = mock(ExamenRepository.class);
         // preguntaRepository = mock(PreguntaRepository.class);
         // examenService = new ExamenServiceImpl(examenRepository, preguntaRepository);
    }

    @Test
    void findExamenPorNombre() {
        // given: preparo el contexto, dependencias, parametros, etc.
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);

        // when: invocamos el método a probar
        Optional<Examen> examen = examenService.findExamenPorNombre("Matemáticas");

        // then: validamos el test
        assertTrue(examen.isPresent());
        assertEquals("Matemáticas", examen.get().getNombre());
    }

    @Test
    void findExamenPorNombreConListaVacía() {
        List<Examen> emptyList = Collections.emptyList();
        when(examenRepository.findAll()).thenReturn(emptyList);

        Optional<Examen> examen = examenService.findExamenPorNombre("Matemáticas");

        assertFalse(examen.isPresent());
    }

    @Test
    void findPreguntasExamenVerificarLLamadas() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        Examen examen = examenService.findExamenPorNombreConPreguntas("Matemáticas");

        assertEquals(5, examen.getPreguntas().size()); // examen matemática tiene 5 preguntas
        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }

    @Test
    void testNumeroDeInvocaciones() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);

        examenService.findExamenPorNombreConPreguntas("Matemáticas");

        verify(preguntaRepository).findPreguntasPorExamenId(5L);
        verify(preguntaRepository, times(1)).findPreguntasPorExamenId(5L);
        verify(preguntaRepository, atLeastOnce()).findPreguntasPorExamenId(5L);
        verify(preguntaRepository, atMostOnce()).findPreguntasPorExamenId(5L);
        verify(preguntaRepository, atLeast(1)).findPreguntasPorExamenId(5L);
        verify(preguntaRepository, atMost(10)).findPreguntasPorExamenId(5L);
        verify(examenRepository, never()).guardar(any(Examen.class));
    }

    @Test
    @DisplayName("VERIFICAR EL GUARDADO DE EXAMEN")
    void testGuardarExamen() {
        // given
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

        // when
        Examen examen = examenService.guardar(newExamen);

        // then
        assertNotNull(examen.getId());
        assertEquals(51L, examen.getId());
        assertEquals("Física", examen.getNombre());
        verify(examenRepository).guardar(any(Examen.class));
        verify(preguntaRepository).guardarVarias(anyList());
    }

    @Test
    void testManejoExceptionFindPreguntasIdNull() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES_ID_NULL);
        when(preguntaRepository.findPreguntasPorExamenId(isNull())).thenThrow(new IllegalArgumentException());

        Exception e = assertThrows(IllegalArgumentException.class, () -> {
           examenService.findExamenPorNombreConPreguntas("Matemáticas");
        });

        assertEquals(IllegalArgumentException.class, e.getClass());
        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(isNull());
    }

    @Test
    void testArgumentMatchers() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        examenService.findExamenPorNombreConPreguntas("Matemáticas"); // id = 5L

        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(argThat(arg -> null != arg && arg.equals(5L)));
    }
}
