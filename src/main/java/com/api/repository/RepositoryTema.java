package com.api.repository;

import com.api.entity.Tema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryTema extends JpaRepository<Tema,Long> {
}
