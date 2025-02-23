package com.api.repository;

import com.api.entity.Asamblea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryAsamblea extends JpaRepository<Asamblea,Long> {
}
