package com.gettasksdone.service.implementation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.gettasksdone.dto.ProyectoDTO;
import com.gettasksdone.model.Proyecto;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.repository.ProyectoRepository;
import com.gettasksdone.service.ProyectoService;
import com.gettasksdone.utils.MHelpers;

@Component
public class ProyectoImpl implements ProyectoService {

    @Autowired
    private ProyectoRepository proyectoRepo;

    @Override
    public List<ProyectoDTO> findAll() {
        List<ProyectoDTO> projectsDTO = new ArrayList<>();
        List<Proyecto> projects = this.proyectoRepo.findAll();
        for(Proyecto project: projects){
            ProyectoDTO projectDTO = MHelpers.modelMapper().map(project, ProyectoDTO.class);
            projectsDTO.add(projectDTO);
        }
        return projectsDTO;
    }

    @Override
    public ProyectoDTO findById(Long id) {
        Optional<Proyecto> project = this.proyectoRepo.findById(id);
        if(project.isEmpty()){
            return null;
        }
        return MHelpers.modelMapper().map(project.get(), ProyectoDTO.class);
    }

    @Override
    public List<ProyectoDTO> findByEstado(String estado) {
        List<ProyectoDTO> projectsDTO = new ArrayList<>();
        List<Proyecto> projects = this.proyectoRepo.findByEstado(estado);
        for(Proyecto project: projects){
            ProyectoDTO projectDTO = MHelpers.modelMapper().map(project, ProyectoDTO.class);
            projectsDTO.add(projectDTO);
        }
        return projectsDTO;
    }

    @Override
    public List<ProyectoDTO> findByInicio(LocalDateTime inicio) {
        List<ProyectoDTO> projectsDTO = new ArrayList<>();
        List<Proyecto> projects = this.proyectoRepo.findByInicio(inicio);
        for(Proyecto project: projects){
            ProyectoDTO projectDTO = MHelpers.modelMapper().map(project, ProyectoDTO.class);
            projectsDTO.add(projectDTO);
        }
        return projectsDTO;
    }

    @Override
    public List<ProyectoDTO> findByFin(LocalDateTime fin) {
        List<ProyectoDTO> projectsDTO = new ArrayList<>();
        List<Proyecto> projects = this.proyectoRepo.findByFin(fin);
        for(Proyecto project: projects){
            ProyectoDTO projectDTO = MHelpers.modelMapper().map(project, ProyectoDTO.class);
            projectsDTO.add(projectDTO);
        }
        return projectsDTO;
    }

    @Override
    public List<ProyectoDTO> findByUsuario(Usuario usuario) {
        List<ProyectoDTO> projectsDTO = new ArrayList<>();
        List<Proyecto> projects = this.proyectoRepo.findByUsuario(usuario);
        for(Proyecto project: projects){
            ProyectoDTO projectDTO = MHelpers.modelMapper().map(project, ProyectoDTO.class);
            projectsDTO.add(projectDTO);
        }
        return projectsDTO;
    }
    
}
