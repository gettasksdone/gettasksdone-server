package com.gettasksdone.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.gettasksdone.dto.CheckItemDTO;
import com.gettasksdone.model.Usuario;

@Service
public interface CheckItemService {
    CheckItemDTO findById(Long id);
    List<CheckItemDTO> findAll();
    List<CheckItemDTO> findByUsuario(Usuario usuario);
}
