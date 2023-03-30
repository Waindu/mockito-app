package org.mycompay.myapp.repositories;

import org.mycompay.myapp.domain.Examen;

import java.util.List;

public interface ExamenRepository {
    Examen guardar(Examen examen);
    List<Examen> findAll();
}
