package com.api.service;

import com.api.entity.FAQ;
import com.api.entity.Usuarios;

import java.util.List;
import java.util.Optional;

public interface ServiceUsuarios {
    public List<Usuarios> mostrarUsuarios();
    public Optional<Usuarios> buscarUsuarioId(Long id);
    public boolean existsUser(String codigo);
    public boolean usuarioFAQ(FAQ faq);
    public List<FAQ> mostrarFAQs(Usuarios usuarios);
    public List<FAQ> mostrarFAQs();
}
