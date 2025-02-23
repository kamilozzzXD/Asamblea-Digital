package com.api.repository;

import com.api.entity.Acta;
import com.api.entity.Asamblea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositoryActa extends JpaRepository<Acta,Long> {
    List<Acta> findByAsamblea(Asamblea asamblea);
}
