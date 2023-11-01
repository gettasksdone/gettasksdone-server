package com.gettasksdone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gettasksdone.model.Etiqueta;
import java.util.List;
import java.util.Optional;
import com.gettasksdone.model.Proyecto;
import com.gettasksdone.model.Tarea;

@Repository
public interface EtiquetaRepository extends JpaRepository<Etiqueta, Long>{
    Optional<Etiqueta> findById(Long id);
    List<Etiqueta> findAll();
    List<Etiqueta> findByProyectosIn(List<Proyecto> proyectos);
    List<Etiqueta> findByTareasIn(List<Tarea> tareas);
}
