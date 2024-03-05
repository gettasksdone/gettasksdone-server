package com.gettasksdone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gettasksdone.model.Usuario;
import java.util.List;
import java.util.Optional;


@Repository
@SuppressWarnings("null")
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    
    List<Usuario> findAll();
    Optional<Usuario> findById(Long id);
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByEmail(String email);
}