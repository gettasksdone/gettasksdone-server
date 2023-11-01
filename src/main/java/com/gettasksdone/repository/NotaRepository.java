package com.gettasksdone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gettasksdone.model.Nota;
import java.util.List;
import java.util.Optional;
import com.gettasksdone.model.Proyecto;
import com.gettasksdone.model.Tarea;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Long>{
    List<Nota> findAll();
    Optional<Nota> findById(Long id);
    List<Nota> findByProyectosIn(List<Proyecto> proyectos);
    List<Nota> findByTareasIn(List<Tarea> tareas);
}