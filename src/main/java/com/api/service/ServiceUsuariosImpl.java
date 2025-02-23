package com.api.service;

import com.api.entity.FAQ;
import com.api.entity.Usuarios;
import com.api.repository.RepositoryFAQ;
import com.api.repository.RepositoryUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class ServiceUsuariosImpl implements ServiceUsuarios{

    @Autowired
    public RepositoryUsuario repositoryUsuario;

    @Autowired
    public RepositoryFAQ repositoryFAQ;


    @Override
    public List<Usuarios> mostrarUsuarios() {
        return repositoryUsuario.findByAuthorityAuthority("ROLE_USER");
    }

    @Override
    public Optional<Usuarios> buscarUsuarioId(Long id) {
        return repositoryUsuario.findById( id);
    }

    @Override
    public boolean existsUser(String codigo) {
        if(repositoryUsuario.findByDocumento(Integer.parseInt(codigo))!=null){
            return true;
        }
        else {return false;}
    }
    @Override
    public boolean usuarioFAQ(FAQ faq){
        try {
            repositoryFAQ.save(faq);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<FAQ> mostrarFAQs(Usuarios usuarios){
        return repositoryFAQ.findFAQByUsuario(usuarios);
    }

    @Override
    public List<FAQ> mostrarFAQs() {
        return repositoryFAQ.findAll();
    }
}
