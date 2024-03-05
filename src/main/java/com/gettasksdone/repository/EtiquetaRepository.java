package com.gettasksdone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gettasksdone.model.Etiqueta;
import com.gettasksdone.model.Usuario;
import java.util.List;
import java.util.Optional;

@Repository
@SuppressWarnings("null")
public interface EtiquetaRepository extends JpaRepository<Etiqueta, Long>{
    Optional<Etiqueta> findById(Long id);
    List<Etiqueta> findAll();
    List<Etiqueta> findByUsuario(Usuario usuario);
}
