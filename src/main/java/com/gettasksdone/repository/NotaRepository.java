package com.gettasksdone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gettasksdone.model.Nota;
import com.gettasksdone.model.Usuario;
import java.util.List;
import java.util.Optional;

@Repository
@SuppressWarnings("null")
public interface NotaRepository extends JpaRepository<Nota, Long>{
    List<Nota> findAll();
    Optional<Nota> findById(Long id);
    List<Nota> findByUsuario(Usuario usuario);
}