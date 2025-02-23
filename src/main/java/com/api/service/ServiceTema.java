package com.api.service;

import com.api.entity.Asamblea;
import com.api.entity.Tema;
import com.api.repository.RepositoryTema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServiceTema {
    @Autowired
    private RepositoryTema temaRepository;

    public Tema crearTema(Tema tema) {
        return temaRepository.save(tema);
    }
    public Optional<Tema> obtenerTemaPorId(long id){
        return temaRepository.findById(id);
    }
}
