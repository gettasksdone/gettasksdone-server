package com.gettasksdone.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.gettasksdone.dto.ContextoDTO;

@Service
public interface ContextoService {
    Optional<ContextoDTO> findById(Long id);
    List<ContextoDTO> findAll();
}
