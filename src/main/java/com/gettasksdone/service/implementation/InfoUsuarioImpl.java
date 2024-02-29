package com.gettasksdone.service.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.gettasksdone.dto.InfoUsuarioDTO;
import com.gettasksdone.model.InfoUsuario;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.repository.InfoUsuarioRepository;
import com.gettasksdone.service.InfoUsuarioService;
import com.gettasksdone.utils.MHelpers;

@Component
public class InfoUsuarioImpl implements InfoUsuarioService {
    
    @Autowired
    private InfoUsuarioRepository infoUsuarioRepo;

    @Override
    public List<InfoUsuarioDTO> findAll() {
        List<InfoUsuarioDTO> infoUsuariosDTO = new ArrayList<>();
        List<InfoUsuario> infoUsuarios = this.infoUsuarioRepo.findAll();
        for(InfoUsuario infoUsuario: infoUsuarios){
            InfoUsuarioDTO infoUsuarioDTO = MHelpers.modelMapper().map(infoUsuario, InfoUsuarioDTO.class);
            infoUsuariosDTO.add(infoUsuarioDTO);
        }
        return infoUsuariosDTO;
    }

    @Override
    public InfoUsuarioDTO findById(Long id) {
        Optional<InfoUsuario> infoUser = this.infoUsuarioRepo.findById(id);
        if(infoUser.isEmpty()){
            return null;
        }
        return MHelpers.modelMapper().map(infoUser, InfoUsuarioDTO.class);
    }

    @Override
    public InfoUsuarioDTO findByUsuario(Usuario usuario) {
        Optional<InfoUsuario> infoUser = this.infoUsuarioRepo.findByUsuario(usuario);
        if(infoUser.isEmpty()){
            return null;
        }
        return MHelpers.modelMapper().map(infoUser, InfoUsuarioDTO.class);
    }

    @Override
    public List<InfoUsuarioDTO> findByNombre(String nombre) {
        List<InfoUsuarioDTO> infoUsuariosDTO = new ArrayList<>();
        List<InfoUsuario> infoUsuarios = this.infoUsuarioRepo.findByNombre(nombre);
        for(InfoUsuario infoUsuario: infoUsuarios){
            InfoUsuarioDTO infoUsuarioDTO = MHelpers.modelMapper().map(infoUsuario, InfoUsuarioDTO.class);
            infoUsuariosDTO.add(infoUsuarioDTO);
        }
        return infoUsuariosDTO;
    }

    @Override
    public List<InfoUsuarioDTO> findByDepartamento(String departamento) {
        List<InfoUsuarioDTO> infoUsuariosDTO = new ArrayList<>();
        List<InfoUsuario> infoUsuarios = this.infoUsuarioRepo.findByDepartamento(departamento);
        for(InfoUsuario infoUsuario: infoUsuarios){
            InfoUsuarioDTO infoUsuarioDTO = MHelpers.modelMapper().map(infoUsuario, InfoUsuarioDTO.class);
            infoUsuariosDTO.add(infoUsuarioDTO);
        }
        return infoUsuariosDTO;
    }
    
}
