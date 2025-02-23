package com.api.controllers;

import com.api.entity.*;
import com.api.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class Controll{

    @Autowired
    public ServiceUsuariosImpl serviceUsuario;

    @Autowired
    public ServiceAsamblea serviceAsamblea;

    @Autowired
    public ServiceTema serviceTema;

    @Autowired
    private ServiceVotacion serviceVotacion;

    @Autowired
    private ServiceActa serviceActa;

    @GetMapping("/")
    public String MostrarIndex() {
        return "login";
    }

    @GetMapping("/login")
    public String Mostrarlogin() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException {
        // Invalidar la sesión actual
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/";
    }

    @GetMapping("/user")
    public String MostrarUser(Model model, Authentication authentication) {
        // Obtener nombre de usuario autenticado
        int username =Integer.parseInt(authentication.getName()) ;

        // Buscar usuario por documento de usuario
        Usuarios usuario = serviceUsuario.repositoryUsuario.findByDocumento(username);

        // Agregar usuario al modelo
        model.addAttribute("usuario", usuario);
        return "user";
    }

    @GetMapping("/user/asambleas")
    public String mostrarAsambleas(Model model) {
        List<Asamblea> asambleas = serviceAsamblea.mostrarAsambleas();

        model.addAttribute("asambleas", asambleas);

        return "asambleasUsuario";
    }

    @GetMapping("/user/asamblea/detalles/{id}")
    public String detallesAsamblea(@PathVariable("id") Long id, Model model) {
        Map<String, Object> detalles = serviceAsamblea.detallesAsamblea(id);
        List<Votacion> votaciones = serviceVotacion.obtenerVotacionesPorIdAsamblea(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // Buscar usuario por documento de usuario
        Usuarios usuario = serviceUsuario.repositoryUsuario.findByDocumento(Integer.parseInt(username));
        boolean asistio = serviceAsamblea.obtenerAsistencia(id, usuario.getId());
        Optional<Asamblea> asamblea=serviceAsamblea.obtenerAsambleaPorId(id);
        List<Acta> actas = serviceActa.listarPorIdAsamblea(asamblea.get());
        model.addAttribute("actas", actas);

        model.addAttribute("asamblea", detalles.get("asamblea"));
        model.addAttribute("tema", detalles.get("tema"));
        model.addAttribute("votaciones", votaciones);
        model.addAttribute("asistio", asistio);
        return "detallesAsambleaUsuario";
    }

    @PostMapping("/user/asambleas/asistencia/{id}")
    public String registrarAsistencia(@PathVariable Long id, @RequestParam String codigo, RedirectAttributes redirectAttributes) {
        Optional<Asamblea> asambleaOptional = serviceAsamblea.obtenerAsambleaPorId(id);

        if (asambleaOptional.isPresent()) {
            Asamblea asamblea = asambleaOptional.get();

            if (asamblea.getCodigo().equals(codigo)) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String username = auth.getName();
                Usuarios usuario = serviceUsuario.repositoryUsuario.findByDocumento(Integer.parseInt(username));

                if (usuario != null) {
                    serviceAsamblea.actualizarAsistencia(asamblea.getId(), usuario.getId(), true);
                    redirectAttributes.addFlashAttribute("mensaje", "Asistencia registrada correctamente.");
                } else {
                    redirectAttributes.addFlashAttribute("mensaje", "Usuario no encontrado.");
                }
            } else {
                redirectAttributes.addFlashAttribute("mensaje", "Código inválido.");
            }
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "Asamblea no encontrada.");
        }

        return "redirect:/user/asamblea/detalles/" + id;
    }





    @GetMapping("/user/asamblea/votacion/{idVotacion}")
    public String mostrarFormularioVotacion(@PathVariable Long idVotacion, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // Buscar usuario por documento de usuario
        Usuarios usuario = serviceUsuario.repositoryUsuario.findByDocumento(Integer.parseInt(username));
        Long usuarioId = usuario.getId();

        Votacion votacion = serviceVotacion.obtenerVotacionPorId(idVotacion);

        // Verificar si la votación ha finalizado
        if ("Terminada".equals(votacion.getEstado())) {
            Long idAsamblea=votacion.getIdAsamblea();
            model.addAttribute("idAsamblea",idAsamblea);
            model.addAttribute("mensaje", "La votación ya finalizó.");
            return "mensajeVotacionFinalizada"; // Crear una vista llamada "mensajeVotacionFinalizada" para mostrar el mensaje
        }

        // Verificar si el usuario ya ha votado en esta votación
        if (serviceVotacion.usuarioHaVotado(usuarioId, idVotacion)) {
            // Obtener resultados de la votación
            List<ResultadoVotacion> resultados = serviceVotacion.obtenerResultadosPorUsuarioYVotacion(usuarioId, idVotacion);

            model.addAttribute("votacion", votacion);
            model.addAttribute("resultados", resultados);
            model.addAttribute("serviceVotacion", serviceVotacion);

            return "resultadoUsuarioVotacion";
        }

        // Si el usuario no ha votado y la votación no ha finalizado, mostrar el formulario
        List<PreguntaVoto> opcionesVoto = serviceVotacion.obtenerOpcionesPorIdVotacion(idVotacion);

        model.addAttribute("votacion", votacion);
        model.addAttribute("opcionesVoto", opcionesVoto);

        return "votarVotacion";
    }



    @PostMapping("/user/asamblea/votacion/{id}/submit")
    public String procesarFormularioVotacion(@PathVariable("id") Long idVotacion,
                                             @RequestParam("preguntaId") List<Long> idsOpciones,
                                             @RequestParam Map<String, String> params,
                                             Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();


        // Se guarda los resultados de la votación
        List<ResultadoVotacion> resultados = new ArrayList<>();
        for (Long idOpcion : idsOpciones) {
            String paramName = "opcion_" + idOpcion;
            if (params.containsKey(paramName)) {
                Usuarios usuario = serviceUsuario.repositoryUsuario.findByDocumento(Integer.parseInt(username));
                Votacion votacion= serviceVotacion.obtenerVotacionPorId(idVotacion);
                Long idSeleccionado = Long.parseLong(params.get(paramName));
                ResultadoVotacion resultado = new ResultadoVotacion();
                resultado.setVotacion(votacion);
                resultado.setUsuario(usuario);
                resultado.setOpcionId(idSeleccionado);

                serviceVotacion.guardarResultado(resultado);
                resultados.add(resultado);
            }
        }
        Votacion votacion = serviceVotacion.obtenerVotacionPorId(idVotacion);
        model.addAttribute("votacion", votacion);
        model.addAttribute("resultados", resultados);
        model.addAttribute("serviceVotacion", serviceVotacion);
        return "resultadoUsuarioVotacion";
    }

    @GetMapping("/user/FAQ")
    public String mostrarFAQs(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Usuarios usuario = serviceUsuario.repositoryUsuario.findByDocumento(Integer.parseInt(username));

        List<FAQ> faqs=serviceUsuario.mostrarFAQs(usuario);
        model.addAttribute("faqs", faqs);
        return "FAQs";
    }

    @GetMapping("/user/FAQ/detalles/{id}")
    public String mostrarDetallesFAQ(@PathVariable("id") Long id, Model model) {
        Optional<FAQ> faqOptional = serviceUsuario.repositoryFAQ.findById(id);
        if (faqOptional.isPresent()) {
            FAQ faq = faqOptional.get();
            model.addAttribute("faq", faq);
            return "detallesFAQ";
        } else {

            return "redirect:/user/FAQ"; //
        }
    }

    @GetMapping("/user/FAQ/crear")
    public String crearFAQ(Model model) {
        model.addAttribute("faq", new FAQ());
        return "formularioFAQ";
    }

    @PostMapping("/user/FAQ/new")
    public String guardarFAQ(@ModelAttribute("faq") FAQ faq, RedirectAttributes redirectAttributes){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Usuarios usuario = serviceUsuario.repositoryUsuario.findByDocumento(Integer.parseInt(username));
        faq.setUsuario(usuario);
        serviceUsuario.usuarioFAQ(faq);


        redirectAttributes.addFlashAttribute("successMessage", "¡Pregunta guardada con éxito!");

        return "redirect:/user/FAQ";
    }


    @GetMapping("/user/asamblea/actaFinalizada/{idAsamblea}")
    public String mostrarActasPorIdAsamblea(Model model, @PathVariable Long idAsamblea) {
        Optional<Asamblea> asamblea=serviceAsamblea.obtenerAsambleaPorId(idAsamblea);
        List<Acta> actas = serviceActa.listarPorIdAsamblea(asamblea.get());
        model.addAttribute("actas", actas);

        return "listarActasUsuario";
    }

    @GetMapping("/user/acta/pdf/{filename:.+}")
    public ResponseEntity<Resource> verPDF(@PathVariable String filename) throws IOException {
        Resource resource = serviceActa.load(filename);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}


