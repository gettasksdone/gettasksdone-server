package com.gettasksdone.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.gettasksdone.dto.NotaDTO;
import com.gettasksdone.model.Usuario;

@Service
public interface NotaService {
    List<NotaDTO> findAll();
    NotaDTO findById(Long id);
    List<NotaDTO> findByUsuario(Usuario usuario);
}
