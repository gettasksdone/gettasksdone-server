package com.gettasksdone.service.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import com.gettaskdone.utils.MHelpers;
import com.gettasksdone.dto.ContextoDTO;
import com.gettasksdone.model.Contexto;
import com.gettasksdone.repository.ContextoRepository;
import com.gettasksdone.service.ContextoService;

public class ContextoImpl implements ContextoService {

    @Autowired
    private ContextoRepository contextoRepo;

    @Override
    public ContextoDTO findById(Long id) {
        Optional<Contexto> context = this.contextoRepo.findById(id);
        if(context.isEmpty()){
            return null;
        }
        return MHelpers.modelMapper().map(context, ContextoDTO.class);
    }

    @Override
    public List<ContextoDTO> findAll() {
        List<ContextoDTO> contextosDTO = new ArrayList<>();
        List<Contexto> contextos = this.contextoRepo.findAll();
        for(Contexto contexto: contextos){
            ContextoDTO contextoDTO = MHelpers.modelMapper().map(contexto, ContextoDTO.class);
            contextosDTO.add(contextoDTO);
        }
        return contextosDTO;
    }
    
}
