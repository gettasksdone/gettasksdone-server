package com.gettasksdone.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.gettasksdone.dto.NotaDTO;

@Service
public interface NotaService {
    List<NotaDTO> findAll();
    NotaDTO findById(Long id);
}
