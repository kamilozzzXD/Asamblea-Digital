package com.api.repository;


import com.api.entity.PreguntaVoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepositoryOpcionVoto extends JpaRepository<PreguntaVoto,Long> {
    List<PreguntaVoto> findByVotacionId(Long votacionId);
}
