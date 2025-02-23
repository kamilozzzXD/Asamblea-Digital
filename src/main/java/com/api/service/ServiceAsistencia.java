package com.api.service;

import com.api.entity.Asistencia;
import com.api.entity.Usuarios;
import com.api.repository.RepositoryAsistencia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceAsistencia {

    @Autowired
    private RepositoryAsistencia asistenciaRepository;

    @Autowired
    private ServiceUsuariosImpl usuarioService;

    public Boolean verificarAsistencia(Long idAsamblea, Long idUsuario) {
        Asistencia asistencia = asistenciaRepository.findByidAsambleaAndIdUsuario(idAsamblea, idUsuario);
        return asistencia != null && asistencia.getAsistencia();
    }
    public List<Usuarios> obtenerAsistentes(Long idAsamblea) {
        List<Usuarios> usuariosAsistentes = new ArrayList<>();

        // Obtener las asistencias para la asamblea específica
        List<Asistencia> asistencias = asistenciaRepository.findByAsistenciaAndIdAsamblea(true, idAsamblea);

        // Obtener los usuarios correspondientes a las asistencias encontradas
        for (Asistencia asistencia : asistencias) {
            Optional<Usuarios> usuarioOptional = usuarioService.buscarUsuarioId(asistencia.getIdUsuario());
            usuarioOptional.ifPresent(usuariosAsistentes::add);
        }

        return usuariosAsistentes;
    }


}
