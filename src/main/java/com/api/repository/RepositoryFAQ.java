package com.api.repository;

import com.api.entity.FAQ;
import com.api.entity.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositoryFAQ extends JpaRepository<FAQ,Long> {
    List<FAQ> findFAQByUsuario(Usuarios usuarios);

}
