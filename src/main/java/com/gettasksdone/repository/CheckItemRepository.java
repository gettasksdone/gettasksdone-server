package com.gettasksdone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gettasksdone.model.CheckItem;
import com.gettasksdone.model.Usuario;
import java.util.List;
import java.util.Optional;

@Repository
@SuppressWarnings("null")
public interface CheckItemRepository extends JpaRepository<CheckItem, Long>{
    Optional<CheckItem> findById(Long id);
    List<CheckItem> findAll();
    List<CheckItem> findByUsuario(Usuario usuario);
}
