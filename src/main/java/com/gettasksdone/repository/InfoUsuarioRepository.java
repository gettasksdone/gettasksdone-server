package com.gettasksdone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.gettasksdone.model.InfoUsuario;
import java.util.List;
import java.util.Optional;

@Repository
public interface InfoUsuarioRepository extends JpaRepository<InfoUsuario, Long>{
    List<InfoUsuario> findAll();
    Optional<InfoUsuario> findById(Long id);
    List<InfoUsuario> findByNombre(@Param("nombre") String nombre);
    List<InfoUsuario> findByDepartamento(@Param("departamento") String departamento);
}