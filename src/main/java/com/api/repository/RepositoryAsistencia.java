package com.api.repository;

import com.api.entity.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryAsistencia extends JpaRepository<Asistencia, Long> {
    int countByAsistenciaAndIdAsamblea(Boolean asistencia, Long idAsamblea);
    Optional<Asistencia> findByIdAsambleaAndIdUsuario(Long idAsamblea, Long idUsuario);
    Asistencia findByidAsambleaAndIdUsuario(Long idAsamblea, Long idUsuario);

    List<Asistencia> findByAsistenciaAndIdAsamblea(Boolean asistencia, Long idAsamblea);
}
