package org.mycompay.myapp;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class ExamenServiceSpyTest {
}
