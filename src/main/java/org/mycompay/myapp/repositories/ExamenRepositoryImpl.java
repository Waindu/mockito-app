package org.mycompay.myapp.repositories;

import org.mycompay.myapp.data.Datos;
import org.mycompay.myapp.domain.Examen;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExamenRepositoryImpl implements ExamenRepository{
    @Override
    public Examen guardar(Examen examen) {
        System.out.println("ExamenRepositoryImpl.guardar");
        return Datos.EXAMEN;
    }

    @Override
    public List<Examen> findAll() {
        System.out.println("ExamenRepositoryImpl.findAll");
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Datos.EXAMENES;
    }
}
