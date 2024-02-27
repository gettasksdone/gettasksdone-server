package com.gettasksdone.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.gettasksdone.dto.InfoUsuarioDTO;
import com.gettasksdone.model.Usuario;

@Service
public interface InfoUsuarioService {
    List<InfoUsuarioDTO> findAll();
    InfoUsuarioDTO findById(Long id);
    InfoUsuarioDTO findByUsuario(Usuario usuario);
    List<InfoUsuarioDTO> findByNombre(String nombre);
    List<InfoUsuarioDTO> findByDepartamento(String departamento);
}
