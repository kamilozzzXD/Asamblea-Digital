package com.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.entity.Usuarios;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositoryUsuario extends JpaRepository<Usuarios,Long> {
  public Usuarios findByDocumento(int documento);
  List<Usuarios> findByAuthorityAuthority(String authority);
}
