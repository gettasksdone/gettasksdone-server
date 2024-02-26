package com.gettasksdone.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.gettasksdone.dto.CheckItemDTO;

@Service
public interface CheckItemService {
    Optional<CheckItemDTO> findById(Long id);
    List<CheckItemDTO> findAll();
}
