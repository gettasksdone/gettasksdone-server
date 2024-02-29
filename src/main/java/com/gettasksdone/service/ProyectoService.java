package com.gettasksdone.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import com.gettasksdone.dto.ProyectoDTO;
import com.gettasksdone.model.Usuario;

@Service
public interface ProyectoService {
    List<ProyectoDTO> findAll();
    ProyectoDTO findById(Long id);
    List<ProyectoDTO> findByEstado(String estado);
    List<ProyectoDTO> findByInicio(LocalDateTime inicio);
    List<ProyectoDTO> findByFin(LocalDateTime fin);
    List<ProyectoDTO> findByUsuario(Usuario usuario);
}
