package com.api.service;

import com.api.entity.Opcion;
import com.api.entity.PreguntaVoto;
import com.api.entity.ResultadoVotacion;
import com.api.entity.Votacion;
import com.api.repository.RepositoryOpcion;
import com.api.repository.RepositoryResultadoVotacion;
import com.api.repository.RepositoryOpcionVoto;
import com.api.repository.RepositoryVotacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceVotacion {
    @Autowired
    private RepositoryOpcionVoto opcionVotoRepository;
    @Autowired
    private RepositoryVotacion votacionRepository;
    @Autowired
    private RepositoryOpcion opcionRepository;
    @Autowired
    private RepositoryResultadoVotacion resultadoVotacionRepository;
    public Votacion crearVotacion(Votacion votacion, List<PreguntaVoto> opciones) {
        Votacion nuevaVotacion = votacionRepository.save(votacion);

        if(opciones!=null) {
            for (PreguntaVoto opcion : opciones) {
                opcion.setIdVotacion(nuevaVotacion.getIdVotacion());
                opcionVotoRepository.save(opcion);
            }
        }
        return nuevaVotacion;
    }
    public Votacion obtenerVotacionPorId(Long idVotacion) {
        return votacionRepository.findById(idVotacion).orElse(null);
    }

    public List<PreguntaVoto> obtenerOpcionesPorIdVotacion(Long idVotacion) {
        return opcionVotoRepository.findByVotacionId(idVotacion);
    }

    public List<Votacion> obtenerVotacionesPorIdAsamblea(Long idAsamblea) {
        return votacionRepository.findByIdAsamblea(idAsamblea);
    }

    public String obtenerPreguntaPorOpcionId(Long id) {
        Optional<Opcion> optionalOpcion = opcionRepository.findById(id);
        if (optionalOpcion.isPresent()) {
            Opcion opcion = optionalOpcion.get();
            if (opcion.getOpcionVoto() != null) {
                return opcion.getOpcionVoto().getPregunta();
            }
        }
        return "";
    }


    public String obtenerOpcionPorId(Long Id) {
        Optional<Opcion> optionalOpcion = opcionRepository.findById(Id);
        if (optionalOpcion.isPresent()) {
            Opcion opcion = optionalOpcion.get();
            return opcion.getOpcion();
        }
        return "";
    }

    public boolean usuarioHaVotado(Long usuarioId, Long votacionId) {
        return resultadoVotacionRepository.existsByUsuarioIdAndVotacionId(usuarioId, votacionId);
    }

    public List<ResultadoVotacion> obtenerResultadosPorUsuarioYVotacion(Long usuarioId, Long votacionId) {
        return resultadoVotacionRepository.findByUsuarioIdAndVotacionId(usuarioId, votacionId);
    }




    public void guardarResultado(ResultadoVotacion resultado) {
        resultadoVotacionRepository.save(resultado);
    }


    public List<Object[]> obtenerResultadosVotacion(Long votacionId) {
        return resultadoVotacionRepository.countVotesByOption(votacionId);
    }

    private static final int MAX_VOTACIONES = 10;

    public boolean puedeCrearVotacion(Long idAsamblea) {
        return obtenerVotacionesPorIdAsamblea(idAsamblea).size() < MAX_VOTACIONES;
    }

    public boolean todasVotacionesFinalizadas(Long idAsamblea) {
        List<Votacion> votaciones = obtenerVotacionesPorIdAsamblea(idAsamblea);

        for (Votacion votacion : votaciones) {
            if (!votacion.getEstado().equals("Terminada")) {
                return false;
            }
        }

        return true;
    }

}
