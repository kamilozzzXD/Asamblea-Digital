package com.api.repository;

import com.api.entity.ResultadoVotacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RepositoryResultadoVotacion extends JpaRepository<ResultadoVotacion, Long> {

    // Método para comprobar si un usuario ha votado en una votación específica
    boolean existsByUsuarioIdAndVotacionId(Long usuarioId, Long votacionId);

    // Método para obtener los resultados de la votación de un usuario específico
    List<ResultadoVotacion> findByUsuarioIdAndVotacionId(Long usuarioId, Long votacionId);

    @Query("SELECT rv.opcionId, COUNT(rv.opcionId) FROM ResultadoVotacion rv WHERE rv.votacion.id = :votacionId GROUP BY rv.opcionId")
    List<Object[]> countVotesByOption(@Param("votacionId") Long votacionId);

}
