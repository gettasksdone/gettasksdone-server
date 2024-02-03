package com.gettasksdone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.gettasksdone.model.Contexto;
import com.gettasksdone.model.Tarea;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long>{
    List<Tarea> findAll();
    Optional<Tarea> findById(Long id);
    List<Tarea> findByEstado(@Param("estado") String estado);
    List<Tarea> findByCreacion(@Param("creacion") LocalDateTime creacion);
    List<Tarea> findByVencimiento(@Param("vencimiento") LocalDateTime vencimiento);
    List<Tarea> findByContexto(@Param("contexto_id") Contexto contexto_id);
    List<Tarea> findByPrioridad(@Param("prioridad") int prioridad);
}