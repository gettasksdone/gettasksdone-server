package com.gettasksdone.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.gettasksdone.dto.ContextoDTO;
import com.gettasksdone.model.Usuario;

@Service
public interface ContextoService {
    ContextoDTO findById(Long id);
    List<ContextoDTO> findAll();
    List<ContextoDTO> findByUsuario(Usuario usuario);
}
