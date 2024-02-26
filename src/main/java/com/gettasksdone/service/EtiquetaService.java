package com.gettasksdone.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.gettasksdone.dto.EtiquetaDTO;

@Service
public interface EtiquetaService {
    Optional<EtiquetaDTO> findById(Long id);
    List<EtiquetaDTO> findAll();
}
