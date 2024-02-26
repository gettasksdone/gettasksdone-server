package com.gettasksdone.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.gettasksdone.dto.ProyectoDTO;

@Service
public interface ProyectoService {
    List<ProyectoDTO> findAll();
    Optional<ProyectoDTO> findById(Long id);
    List<ProyectoDTO> findByEstado(String estado);
    List<ProyectoDTO> findByInicio(LocalDateTime inicio);
    List<ProyectoDTO> findByFin(LocalDateTime fin);
}
