package com.gettasksdone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.gettasksdone.model.Proyecto;
import com.gettasksdone.model.Usuario;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long>{
    List<Proyecto> findAll();
    Optional<Proyecto> findById(Long id);
    List<Proyecto> findByEstado(@Param("estado") String estado);
    List<Proyecto> findByInicio(@Param("inicio") LocalDateTime inicio);
    List<Proyecto> findByFin(@Param("fin") LocalDateTime fin);
    List<Proyecto> findByUsuario(Usuario usuario);
}