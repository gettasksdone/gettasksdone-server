package com.gettasksdone.service.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.gettasksdone.dto.EtiquetaDTO;
import com.gettasksdone.model.Etiqueta;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.repository.EtiquetaRepository;
import com.gettasksdone.service.EtiquetaService;
import com.gettasksdone.utils.MHelpers;

@Component
public class EtiquetaImpl implements EtiquetaService {

    @Autowired
    private EtiquetaRepository etiquetaRepo;

    @Override
    public EtiquetaDTO findById(Long id) {
        Optional<Etiqueta> etiqueta = this.etiquetaRepo.findById(id);
        if(etiqueta.isEmpty()){
            return null;
        }
        return MHelpers.modelMapper().map(etiqueta.get(), EtiquetaDTO.class);
    }

    @Override
    public List<EtiquetaDTO> findAll() {
        List<EtiquetaDTO> etiquetasDTO = new ArrayList<>();
        List<Etiqueta> etiquetas = this.etiquetaRepo.findAll();
        for(Etiqueta etiqueta: etiquetas){
            EtiquetaDTO etiquetaDTO = MHelpers.modelMapper().map(etiqueta, EtiquetaDTO.class);
            etiquetasDTO.add(etiquetaDTO);
        }
        return etiquetasDTO;
    }

    @Override
    public List<EtiquetaDTO> findByUsuario(Usuario usuario) {
        List<EtiquetaDTO> etiquetasDTO = new ArrayList<>();
        List<Etiqueta> etiquetas = this.etiquetaRepo.findByUsuario(usuario);
        for(Etiqueta etiqueta: etiquetas){
            EtiquetaDTO etiquetaDTO = MHelpers.modelMapper().map(etiqueta, EtiquetaDTO.class);
            etiquetasDTO.add(etiquetaDTO);
        }
        return etiquetasDTO;
    }
    
}
