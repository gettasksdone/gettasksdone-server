package com.gettasksdone.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import com.gettasksdone.dto.TareaDTO;
import com.gettasksdone.model.Contexto;
import com.gettasksdone.model.Usuario;

@Service
public interface TareaService {
    List<TareaDTO> findAll();
    TareaDTO findById(Long id);
    List<TareaDTO> findByEstado(String estado);
    List<TareaDTO> findByCreacion(LocalDateTime creacion);
    List<TareaDTO> findByVencimiento(LocalDateTime vencimiento);
    List<TareaDTO> findByContexto(Contexto contexto);
    List<TareaDTO> findByPrioridad(int prioridad);
    List<TareaDTO> findByUsuario(Usuario usuario);
}
