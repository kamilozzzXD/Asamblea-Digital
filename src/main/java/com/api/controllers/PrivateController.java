package com.api.controllers;

import com.api.entity.*;
import com.api.repository.RepositoryAuthority;
import com.api.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.naming.Binding;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@Controller
public class PrivateController {

    @Autowired
    public ServiceUsuariosImpl serviceUsuario;

    @Autowired
    public RepositoryAuthority authority;

    @Autowired
    private ServiceAsamblea serviceAsamblea;

    @Autowired
    private ServiceTema serviceTema;

    @Autowired
    private ServiceVotacion serviceVotacion;

    @Autowired
    private ServiceAsistencia serviceAsistencia;

    @Autowired
    private ServiceActa serviceActa;

    //INDEX ADMIN
    @GetMapping("/admin")
    public String MostrarAdmin(Model model, Authentication authentication) {
        // Obtener nombre de usuario autenticado
        String username = authentication.getName();

        // Buscar usuario por documento de usuario
        Usuarios usuario = serviceUsuario.repositoryUsuario.findByDocumento(Integer.parseInt(username));

        // Agregar usuario al modelo
        model.addAttribute("usuario", usuario);

        return "admin";
    }

    @GetMapping("/admin/asambleas")
    public String mostrarAsambleas(Model model) {
        List<Asamblea> asambleas = serviceAsamblea.mostrarAsambleas();

        model.addAttribute("asambleas", asambleas);

        return "asambleas";
    }

    @GetMapping("/admin/asambleas/new")
    public String nuevaAsambleas(Model model){
        model.addAttribute("asamblea", new Asamblea());
        return "formularioAsamblea";
    }

    @PostMapping("/admin/asambleas/crear")
    public String crearAsamblea(@ModelAttribute("asamblea") Asamblea asamblea,
                                @RequestParam("descripcionTema") List<String> descripcionesTema,
                                Model model, RedirectAttributes redirectAttributes) {

        if (!serviceAsamblea.puedeCrearAsamblea()) {
            redirectAttributes.addFlashAttribute("errorMessage", "No se puede crear más asambleas, límite alcanzado.");
            return "redirect:/admin/asambleas";
        }

        serviceAsamblea.crearAsamblea(asamblea, descripcionesTema, model);
        return "redirect:/admin/asambleas";
    }


    @PostMapping("/admin/asamblea/generarCodigo/{id}")
    public String generarCodigoAcceso(@PathVariable Long id) {
        serviceAsamblea.generarCodigoAcceso(id);
        return "redirect:/admin/asamblea/detalles/" + id;
    }

    @GetMapping("/admin/asamblea/detalles/{id}")
    public String detallesAsamblea(@PathVariable("id") Long id, Model model) {
        Map<String, Object> detalles = serviceAsamblea.detallesAsamblea(id);
        List<Votacion> votaciones = serviceVotacion.obtenerVotacionesPorIdAsamblea(id);
        int totalAsistentes= serviceAsamblea.totalAsistentes(id);
        int totalMiembros= serviceAsamblea.totalMiembros();
        int porcentajeAsistentes = (totalMiembros == 0) ? 0 : (totalAsistentes * 100 / totalMiembros);

        // Preparar el texto para mostrar en la vista
        String textoAsistentes = totalAsistentes + " (" + porcentajeAsistentes + "%)";
        Optional<Asamblea> asamblea=serviceAsamblea.obtenerAsambleaPorId(id);
        List<Acta> actas = serviceActa.listarPorIdAsamblea(asamblea.get());
        model.addAttribute("actas", actas);
        model.addAttribute("asamblea", detalles.get("asamblea"));
        model.addAttribute("tema", detalles.get("tema"));
        model.addAttribute("votaciones", votaciones);
        model.addAttribute("totalAsistentes", textoAsistentes);
        model.addAttribute("totalMiembros", totalMiembros);

        return "detallesAsamblea";
    }

    @PostMapping("/admin/asambleas/estado/{id}")
    public String estadoAsamblea(@PathVariable("id") Long id, @RequestParam("estado") String estado, Model model) {
        Asamblea asamblea = serviceAsamblea.obtenerAsambleaPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid asamblea Id:" + id));

        boolean todasFinalizadas = serviceVotacion.todasVotacionesFinalizadas(id);

        if (!todasFinalizadas) {
            model.addAttribute("mensajeError", "No se puede finalizar la asamblea porque hay votaciones pendientes.");
            return "redirect:/admin/asamblea/detalles/" + id;
        }

        asamblea.setEstado(estado);
        serviceAsamblea.actualizarAsamblea(asamblea);

        return "redirect:/admin/asamblea/detalles/" + id;
    }



    @GetMapping("/admin/asambleas/asistencia/{id}")
    public String mostrarAsistencia(@PathVariable Long id, Model model) {
        Asamblea asamblea = serviceAsamblea.obtenerAsambleaPorId(id).orElseThrow(() -> new IllegalArgumentException("Invalid asamblea Id:" + id));
        List<Usuarios> usuarios = serviceUsuario.mostrarUsuarios();

        // Obtener los valores de asistencia desde la base de datos
        Map<Long, Boolean> asistenciaValues = new HashMap<>();
        for (Usuarios usuario : usuarios) {
            boolean asistio = serviceAsamblea.obtenerAsistencia(id, usuario.getId());
            asistenciaValues.put(usuario.getId(), asistio);
        }

        AsistenciaForm asistenciaForm = new AsistenciaForm();
        asistenciaForm.setIdAsamblea(id);
        asistenciaForm.setAsistenciaValues(asistenciaValues);

        model.addAttribute("asamblea", asamblea);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("asistenciaForm", asistenciaForm);

        return "asistencia";
    }

    @GetMapping("/admin/asambleas/asistentes/{id}")
    public String mostrarAsistentes(@PathVariable Long id, Model model) {
        Asamblea asamblea = serviceAsamblea.obtenerAsambleaPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid asamblea Id:" + id));

        List<Usuarios> usuariosAsistentes = serviceAsistencia.obtenerAsistentes(id);
        model.addAttribute("usuarios", usuariosAsistentes);
        model.addAttribute("asamblea", asamblea);

        return "asistentes";
    }


    

    @PostMapping("/admin/asambleas/asistencia/{id}")
    public String guardarAsistencia(
            @PathVariable Long id,
            @ModelAttribute("asistenciaForm") AsistenciaForm asistenciaForm) {

        for (Map.Entry<Long, Boolean> entry : asistenciaForm.getAsistenciaValues().entrySet()) {
            serviceAsamblea.actualizarAsistencia(id, entry.getKey(), entry.getValue());
        }

        return "redirect:/admin/asamblea/detalles/" + id;
    }

    @PostMapping("/admin/asamblea/comenzar/{id}")
    public String comenzarAsamblea(@PathVariable Long id, Model model) {
        Asamblea asamblea = serviceAsamblea.obtenerAsambleaPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid asamblea Id:" + id));
        List<Usuarios> usuarios = serviceUsuario.mostrarUsuarios();

        // Contar la cantidad de asistentes y calcular el porcentaje
        int totalUsuarios = usuarios.size();
        int asistentes = serviceAsamblea.totalAsistentes(id);

        // Calcular el porcentaje de asistentes
        int porcentajeAsistentes = (totalUsuarios == 0) ? 0 : (asistentes * 100 / totalUsuarios);

        // Preparar el texto para mostrar en la vista
        String textoAsistentes = asistentes + " (" + porcentajeAsistentes + "%)";

        // Agregar al modelo
        model.addAttribute("totalAsistentes", textoAsistentes);
        model.addAttribute("totalMiembros", totalUsuarios);

        // Verificar si el 51% de los usuarios asistió para iniciar la asamblea
        if (porcentajeAsistentes >= 51) {
            asamblea.setEstado("Iniciada");
            serviceAsamblea.actualizarAsamblea(asamblea);

            // Agregar mensaje de confirmación al modelo
            model.addAttribute("mensajeConfirmacion", "Asamblea iniciada con éxito.");

            // Actualizar los detalles de la asamblea en el modelo
            Map<String, Object> detalles = serviceAsamblea.detallesAsamblea(id);
            model.addAttribute("asamblea", detalles.get("asamblea"));
            model.addAttribute("tema", detalles.get("tema"));

            return "detallesAsamblea";
        } else {
            model.addAttribute("mensajeError", "No se puede iniciar la Asamblea, asistentes incompletos");
            Map<String, Object> detalles = serviceAsamblea.detallesAsamblea(id);
            model.addAttribute("asamblea", detalles.get("asamblea"));
            model.addAttribute("tema", detalles.get("tema"));
            return "detallesAsamblea";
        }
    }





    @GetMapping("/admin/asamblea/votacion/crear")
    public String mostrarFormularioCreacion(@RequestParam Long idAsamblea, Model model) {
        Votacion votacion = new Votacion();
        votacion.setIdAsamblea(idAsamblea);
        votacion.setEstado("Comenzada");

        PreguntaVoto preguntaVoto = new PreguntaVoto();
        model.addAttribute("votacion", votacion);
        model.addAttribute("opcionVoto", preguntaVoto);
        model.addAttribute("idAsamblea", idAsamblea);

        return "formularioVotacion";
    }

    @PostMapping("/admin/asamblea/votacion/crear/new")
    public String crearVotacion(@ModelAttribute Votacion votacion,
                                @RequestParam("preguntas[]") List<String> preguntas,
                                @RequestParam("tipoOpciones[]") List<String> tipoOpciones,
                                @RequestParam("opciones[]") List<String> opcionesList,
                                @RequestParam("contadorOpciones[]") List<Integer> contadorOpciones,
                                RedirectAttributes redirectAttributes) {

        if (!serviceVotacion.puedeCrearVotacion(votacion.getIdAsamblea())) {
            redirectAttributes.addFlashAttribute("errorMessage", "No se puede crear más votaciones, límite alcanzado.");
            return "redirect:/admin/asamblea/detalles/" + votacion.getIdAsamblea();
        }
        List<PreguntaVoto> opcionesVotos = new ArrayList<>();

        int indexOpciones = 0; // Índice para recorrer la lista de opciones

        for (int i = 0; i < preguntas.size(); i++) {
            PreguntaVoto nuevaPreguntaVoto = new PreguntaVoto();
            nuevaPreguntaVoto.setPregunta(preguntas.get(i));
            nuevaPreguntaVoto.setTipoOpcion(tipoOpciones.get(i));

            // Separar las opciones para esta pregunta
            List<String> listaOpciones = new ArrayList<>();
            for (int j = 0; j < contadorOpciones.get(i); j++) {
                listaOpciones.add(opcionesList.get(indexOpciones));
                indexOpciones++;
            }

            List<Opcion> opciones = new ArrayList<>();
            for (String opcionStr : listaOpciones) {
                Opcion opcion = new Opcion();
                opcion.setOpcion(opcionStr.trim());
                opcion.setOpcionVoto(nuevaPreguntaVoto);
                opciones.add(opcion);
            }

            nuevaPreguntaVoto.setOpciones(opciones);
            nuevaPreguntaVoto.setIdVotacion(votacion.getIdVotacion());

            opcionesVotos.add(nuevaPreguntaVoto);
        }

        Votacion nuevaVotacion = serviceVotacion.crearVotacion(votacion, opcionesVotos);

        return "redirect:/admin/asamblea/votacion/ver/" + nuevaVotacion.getIdVotacion();
    }


    @GetMapping("/admin/asamblea/votacion/ver/{idVotacion}")
    public String mostrarVotacion(@PathVariable Long idVotacion, Model model) {
        Votacion votacion = serviceVotacion.obtenerVotacionPorId(idVotacion);
        List<PreguntaVoto> opcionesVoto = serviceVotacion.obtenerOpcionesPorIdVotacion(idVotacion);
        List<Object[]> resultados = serviceVotacion.obtenerResultadosVotacion(idVotacion);

        model.addAttribute("votacion", votacion);
        model.addAttribute("opcionesVoto", opcionesVoto);
        model.addAttribute("resultados", resultados);
        model.addAttribute("resultadoHelper", new ResultadoHelper(resultados)); // Agregar helper para obtener resultado

        return "verVotacion";
    }




    @PostMapping("/admin/asamblea/votacion/ver/terminar/{idVotacion}")
    public String terminarVotacion(@PathVariable Long idVotacion) {
        Votacion votacion = serviceVotacion.obtenerVotacionPorId(idVotacion);
        if (votacion != null) {
            votacion.setEstado("Terminada");
            serviceVotacion.crearVotacion(votacion, null);
        }
        return "redirect:/admin/asamblea/votacion/ver/" + votacion.getIdVotacion();
    }

    @GetMapping("/admin/FAQ")
    public String mostrarFAQs(Model model) {
        List<FAQ> faqs = serviceUsuario.mostrarFAQs();

        List<String> nombresUsuarios = new ArrayList<>();
        for (FAQ faq : faqs) {
            Optional<Usuarios> usuarioOptional = serviceUsuario.buscarUsuarioId(faq.getUsuario().getId());
            if (usuarioOptional.isPresent()) {
                nombresUsuarios.add(usuarioOptional.get().getNombre());
            } else {
                nombresUsuarios.add("Usuario no encontrado");
            }
        }

        model.addAttribute("faqs", faqs);
        model.addAttribute("nombresUsuarios", nombresUsuarios);

        return "FAQsAdmin";
    }



    @GetMapping("/admin/FAQ/responder/{id}")
    public String mostrarFormularioResponderFAQ(@PathVariable("id") Long id, Model model) {
        Optional<FAQ> faqOptional = serviceUsuario.repositoryFAQ.findById(id);
        if (faqOptional.isPresent()) {
            FAQ faq = faqOptional.get();
            model.addAttribute("faq", faq);
            return "formularioResponderFAQ";
        } else {

            return "redirect:/admin/FAQ";
        }
    }

    @PostMapping("/admin/FAQ/responder")
    public String responderFAQ(@ModelAttribute("faq") FAQ faq, RedirectAttributes redirectAttributes,Authentication authentication) {
        String username = authentication.getName();

        // Buscar usuario por documento de usuario
        Usuarios usuario = serviceUsuario.repositoryUsuario.findByDocumento(Integer.parseInt(username));
        faq.setResponde(usuario.getNombre());
        faq.setRevision(true);

        serviceUsuario.repositoryFAQ.save(faq);
        redirectAttributes.addFlashAttribute("successMessage", "La pregunta ha sido respondida exitosamente.");

        return "redirect:/admin/FAQ";
    }

    @GetMapping("/admin/asamblea/acta/{idAsamblea}")
    public String mostrarActa(Model model, @PathVariable Long idAsamblea) {

        model.addAttribute("acta",new Acta());
        model.addAttribute("id",idAsamblea);

        return "acta";
    }

    @PostMapping("/admin/asamblea/acta/guardar")
    public String guardarActa(@Valid @ModelAttribute Acta acta,
                              @RequestParam("file") MultipartFile file,
                              @RequestParam("idAsamblea") Long idAsamblea,
                              RedirectAttributes redirectAttributes) throws IOException {

        if (!serviceActa.puedeCrearActa(idAsamblea)) {
            redirectAttributes.addFlashAttribute("errorMessage", "No se puede crear más actas, límite alcanzado.");
            return "redirect:/admin/asamblea/detalles/" + idAsamblea;
        }

        if (!file.isEmpty()) {
            if (acta.getActa() != null) {
                serviceActa.deleteFile(acta.getActa());
            }
            String uniqueFileName = serviceActa.copy(file);
            acta.setActa(uniqueFileName);
        }

        Asamblea asamblea = serviceAsamblea.obtenerAsambleaPorId(idAsamblea)
                .orElseThrow(() -> new IllegalArgumentException("Invalid asamblea Id:" + idAsamblea));
        acta.setAsamblea(asamblea);
        serviceActa.guardar(acta);

        return "redirect:/admin/asamblea/detalles/" + idAsamblea;
    }

    @GetMapping("/admin/asamblea/actaFinalizada/{idAsamblea}")
    public String mostrarActasPorIdAsamblea(Model model, @PathVariable Long idAsamblea) {
        Optional<Asamblea> asamblea=serviceAsamblea.obtenerAsambleaPorId(idAsamblea);
        List<Acta> actas = serviceActa.listarPorIdAsamblea(asamblea.get());
        model.addAttribute("actas", actas);

        return "listarActas";
    }


    @GetMapping("/admin/acta/pdf/{filename:.+}")
    public ResponseEntity<Resource> verPDF(@PathVariable String filename) throws IOException {
        Resource resource = serviceActa.load(filename);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }




    public class AsistenciaForm {
        private Long idAsamblea;
        private Map<Long, Boolean> asistenciaValues;

        public Long getIdAsamblea() {
            return idAsamblea;
        }

        public void setIdAsamblea(Long idAsamblea) {
            this.idAsamblea = idAsamblea;
        }

        public Map<Long, Boolean> getAsistenciaValues() {
            return asistenciaValues;
        }

        public void setAsistenciaValues(Map<Long, Boolean> asistenciaValues) {
            this.asistenciaValues = asistenciaValues;
        }
    }


    public class ResultadoHelper {

        private final List<Object[]> resultados;

        public ResultadoHelper(List<Object[]> resultados) {
            this.resultados = resultados;
        }

        public Long obtenerResultado(Long opcionId) {
            for (Object[] resultado : resultados) {
                Long id = (Long) resultado[0];
                if (id.equals(opcionId)) {
                    return (Long) resultado[1];
                }
            }
            return 0L;
        }
    }



}
