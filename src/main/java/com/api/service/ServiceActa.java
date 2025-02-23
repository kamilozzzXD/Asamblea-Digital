package com.api.service;

import com.api.entity.Acta;
import com.api.entity.Asamblea;
import com.api.repository.RepositoryActa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ServiceActa {
    private final static String UPLOADS_FOLDER = "src/main/resources/static/acta";
    @Autowired
    private RepositoryActa actaRepository;

    @Autowired
    private ServiceAsamblea serviceAsamblea;
    public void guardar(Acta acta){
        actaRepository.save(acta);
    }

    public List<Acta>listar(){return actaRepository.findAll();}

    public void eliminarById(long id){
        actaRepository.deleteById(id);
    }

    public Acta mostrar(long id){
        return actaRepository.findById(id).get();
    }
    public List<Acta> listarPorIdAsamblea(Asamblea asamblea) {
        return actaRepository.findByAsamblea(asamblea);
    }


    public Resource load(String filename) throws MalformedURLException {
        Path path = getPath(filename);
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("Error in path: " + path.toString());
        }
        return resource;
    }


    public String copy(MultipartFile file) throws IOException {
        String uniqueFilename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path rootPath = getPath(uniqueFilename);
        Files.copy(file.getInputStream(), rootPath);
        return uniqueFilename;
    }

    public boolean deleteFile(String filename) {
        Path rootPath = getPath(filename);
        File file = rootPath.toFile();
        if(file.exists() && file.canRead()) {
            if(file.delete()) {
                return true;
            }
        }
        return false;
    }

    public Path getPath(String filename) {
        return Paths.get(UPLOADS_FOLDER).resolve(filename).toAbsolutePath();
    }

    private static final int MAX_ACTAS = 10;
    // Otros métodos y variables del servicio

    public boolean puedeCrearActa(Long idAsamblea) {
        Asamblea asamblea = serviceAsamblea.obtenerAsambleaPorId(idAsamblea)
                .orElseThrow(() -> new IllegalArgumentException("Invalid asamblea Id:" + idAsamblea));
        return listarPorIdAsamblea(asamblea).size() < MAX_ACTAS;
    }
}
