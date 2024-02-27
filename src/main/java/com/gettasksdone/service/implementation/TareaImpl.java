package com.gettasksdone.service.implementation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import com.gettaskdone.utils.MHelpers;
import com.gettasksdone.dto.TareaDTO;
import com.gettasksdone.model.Contexto;
import com.gettasksdone.model.Tarea;
import com.gettasksdone.repository.TareaRepository;
import com.gettasksdone.service.TareaService;

public class TareaImpl implements TareaService{

    @Autowired
    private TareaRepository tareaRepo;

    @Override
    public List<TareaDTO> findAll() {
        List<TareaDTO> tasksDTO = new ArrayList<>();
        List<Tarea> tasks = this.tareaRepo.findAll();
        for(Tarea task: tasks){
            TareaDTO taskDTO = MHelpers.modelMapper().map(task, TareaDTO.class);
            tasksDTO.add(taskDTO);
        }
        return tasksDTO;
    }

    @Override
    public TareaDTO findById(Long id) {
        Optional<Tarea> task = this.tareaRepo.findById(id);
        if(task.isEmpty()){
            return null;
        }
        return MHelpers.modelMapper().map(task.get(), TareaDTO.class);
    }

    @Override
    public List<TareaDTO> findByEstado(String estado) {
        List<TareaDTO> tasksDTO = new ArrayList<>();
        List<Tarea> tasks = this.tareaRepo.findByEstado(estado);
        for(Tarea task: tasks){
            TareaDTO taskDTO = MHelpers.modelMapper().map(task, TareaDTO.class);
            tasksDTO.add(taskDTO);
        }
        return tasksDTO;
    }

    @Override
    public List<TareaDTO> findByCreacion(LocalDateTime creacion) {
        List<TareaDTO> tasksDTO = new ArrayList<>();
        List<Tarea> tasks = this.tareaRepo.findByCreacion(creacion);
        for(Tarea task: tasks){
            TareaDTO taskDTO = MHelpers.modelMapper().map(task, TareaDTO.class);
            tasksDTO.add(taskDTO);
        }
        return tasksDTO;
    }

    @Override
    public List<TareaDTO> findByVencimiento(LocalDateTime vencimiento) {
        List<TareaDTO> tasksDTO = new ArrayList<>();
        List<Tarea> tasks = this.tareaRepo.findByVencimiento(vencimiento);
        for(Tarea task: tasks){
            TareaDTO taskDTO = MHelpers.modelMapper().map(task, TareaDTO.class);
            tasksDTO.add(taskDTO);
        }
        return tasksDTO;
    }

    @Override
    public List<TareaDTO> findByContexto(Contexto contexto) {
        List<TareaDTO> tasksDTO = new ArrayList<>();
        List<Tarea> tasks = this.tareaRepo.findByContexto(contexto);
        for(Tarea task: tasks){
            TareaDTO taskDTO = MHelpers.modelMapper().map(task, TareaDTO.class);
            tasksDTO.add(taskDTO);
        }
        return tasksDTO;
    }

    @Override
    public List<TareaDTO> findByPrioridad(int prioridad) {
        List<TareaDTO> tasksDTO = new ArrayList<>();
        List<Tarea> tasks = this.tareaRepo.findByPrioridad(prioridad);
        for(Tarea task: tasks){
            TareaDTO taskDTO = MHelpers.modelMapper().map(task, TareaDTO.class);
            tasksDTO.add(taskDTO);
        }
        return tasksDTO;
    }
    
}
