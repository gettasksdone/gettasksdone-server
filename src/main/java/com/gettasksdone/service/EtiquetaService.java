package com.gettasksdone.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.gettasksdone.dto.EtiquetaDTO;

@Service
public interface EtiquetaService {
    EtiquetaDTO findById(Long id);
    List<EtiquetaDTO> findAll();
}
