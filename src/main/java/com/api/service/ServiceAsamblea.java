package com.api.service;

import com.api.entity.*;
import com.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.*;

@Service
public class ServiceAsamblea {
    @Autowired
    private RepositoryAsamblea asambleaRepository;
    @Autowired
    private RepositoryTema temaRepository;
    @Autowired
    private RepositoryAsistencia asistenciaRepository;
    @Autowired
    private ServiceUsuariosImpl usuarioService;

    public Asamblea crearAsamblea(Asamblea asamblea, List<String> descripcionesTema, Model model) {
        // Concatenar todos los temas en una sola cadena
        String descripcionConcatenada = "";
        if (descripcionesTema != null && !descripcionesTema.isEmpty()) {
            descripcionConcatenada = String.join(",", descripcionesTema);
        }

        Tema tema = new Tema();
        tema.setTemaDiscusion(descripcionConcatenada);
        Tema nuevoTema = temaRepository.save(tema);

        // Asignar el ID del tema recién creado a la asamblea
        asamblea.setTema(nuevoTema);

        Asamblea nuevaAsamblea = asambleaRepository.save(asamblea);

        // Agregar la nueva asamblea y el nuevo tema al modelo
        model.addAttribute("nuevaAsamblea", nuevaAsamblea);
        model.addAttribute("nuevoTema", nuevoTema);

        return nuevaAsamblea;
    }

    public void generarCodigoAcceso(Long asambleaId) {
        Optional<Asamblea> asambleaOptional = asambleaRepository.findById(asambleaId);
        if (asambleaOptional.isPresent()) {
            Asamblea asamblea = asambleaOptional.get();
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            String codigo = bCryptPasswordEncoder.encode(asamblea.getFecha().toString());
            codigo = codigo.substring(codigo.length() - 5);
            asamblea.setCodigo(codigo);
            actualizarAsamblea(asamblea);
        }
    }

    public void actualizarAsamblea(Asamblea asamblea) {
        asambleaRepository.save(asamblea);
    }


    public List<Asamblea> mostrarAsambleas() {
        List<Asamblea> asambleas = asambleaRepository.findAll();

        // Ordenar las asambleas según el estado y la fecha
        asambleas.sort((a1, a2) -> {
            // Comparar los estados
            int estadoComparison = getEstadoValue(a1.getEstado()) - getEstadoValue(a2.getEstado());

            // Si los estados son diferentes, retorna el resultado de la comparación de estados
            if (estadoComparison != 0) {
                return estadoComparison;
            }

            // Si los estados son iguales, compara las fechas
            return a1.getFecha().compareTo(a2.getFecha());
        });

        return asambleas;
    }

    private int getEstadoValue(String estado) {
        switch (estado) {
            case "Iniciada":
                return 0;
            case "Programada":
                return 1;
            case "Finalizada":
                return 2;
            case "Cancelada":
                return 3;
            default:
                return 4; // En caso de un estado desconocido
        }
    }

    public Optional<Asamblea> obtenerAsambleaPorId(long id){
        return asambleaRepository.findById(id);
    }

    public Map<String, Object> detallesAsamblea(long id){
        Map<String, Object> detalles = new HashMap<>();

        Optional<Asamblea> asambleaOptional = asambleaRepository.findById(id);

        if (asambleaOptional.isPresent()) {
            Asamblea asamblea = asambleaOptional.get();
            detalles.put("asamblea", asamblea);

            // Obtener el tema asociado a la asamblea
            Optional<Tema> temaOptional = temaRepository.findById(asamblea.getTema().getId());

            if (temaOptional.isPresent()) {
                Tema tema = temaOptional.get();
                detalles.put("tema", tema);
            }
        }

        return detalles;
    }

    public Asistencia actualizarAsistencia(Long idAsamblea, Long idUsuario, Boolean asistenciaValue) {
        Optional<Asistencia> asistenciaOptional = asistenciaRepository.findByIdAsambleaAndIdUsuario(idAsamblea, idUsuario);
        if (asistenciaOptional.isPresent()) {
            Asistencia asistencia = asistenciaOptional.get();
            asistencia.setAsistencia(asistenciaValue);
            return asistenciaRepository.save(asistencia);
        } else {
            return marcarAsistencia(idAsamblea, idUsuario, asistenciaValue);
        }
    }


    public Asistencia marcarAsistencia(Long idAsamblea,Long idUsuario, Boolean asistenciaValue){
        Asistencia asistencia = new Asistencia();
        asistencia.setIdAsamblea(idAsamblea);
        asistencia.setIdUsuario(idUsuario);
        asistencia.setAsistencia(asistenciaValue);

        return asistenciaRepository.save(asistencia);
    }
    public boolean obtenerAsistencia(Long idAsamblea, Long idUsuario) {
        Optional<Asistencia> asistenciaOptional = asistenciaRepository.findByIdAsambleaAndIdUsuario(idAsamblea, idUsuario);
        return asistenciaOptional.map(Asistencia::getAsistencia).orElse(false);
    }

    public int totalAsistentes(Long idAsamblea){
        return asistenciaRepository.countByAsistenciaAndIdAsamblea(true,idAsamblea);
    }

    public int totalMiembros(){
        return usuarioService.mostrarUsuarios().size();
    }

    private static final int MAX_ASAMBLEAS = 20;

    public boolean puedeCrearAsamblea() {
        return mostrarAsambleas().size() < MAX_ASAMBLEAS;
    }

}
