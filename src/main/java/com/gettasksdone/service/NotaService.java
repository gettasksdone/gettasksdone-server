package com.gettasksdone.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.gettasksdone.dto.NotaDTO;

@Service
public interface NotaService {
    List<NotaDTO> findAll();
    Optional<NotaDTO> findById(Long id);
}
