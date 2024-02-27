package com.gettasksdone.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.gettasksdone.dto.ContextoDTO;

@Service
public interface ContextoService {
    ContextoDTO findById(Long id);
    List<ContextoDTO> findAll();
}
