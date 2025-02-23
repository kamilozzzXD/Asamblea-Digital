package com.api.repository;

import com.api.entity.Votacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepositoryVotacion extends JpaRepository<Votacion,Long> {
    List<Votacion> findByIdAsamblea(Long idAsamblea);
}
