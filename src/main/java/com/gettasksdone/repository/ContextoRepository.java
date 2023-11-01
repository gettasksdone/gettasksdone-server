package com.gettasksdone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gettasksdone.model.Contexto;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContextoRepository extends JpaRepository<Contexto, Long>{
    Optional<Contexto> findById(Long id);
    List<Contexto> findAll();
}
