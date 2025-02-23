package com.api.repository;

import com.api.entity.Opcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryOpcion extends JpaRepository<Opcion, Long> {

}
