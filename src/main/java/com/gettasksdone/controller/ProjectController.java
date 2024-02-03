package com.gettasksdone.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import com.gettasksdone.model.Proyecto;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.model.Etiqueta;
import com.gettasksdone.repository.ProyectoRepository;
import com.gettasksdone.repository.UsuarioRepository;
import com.gettasksdone.repository.EtiquetaRepository;

@RestController
@RequestMapping("/project")
public class ProjectController {
    
    @Autowired
    private ProyectoRepository proyectoRepo;
    @Autowired
    private EtiquetaRepository etiquetaRepo;
    @Autowired
    private UsuarioRepository usuarioRepo;

    @GetMapping("/getProjects")
	public List<Proyecto> allProjects(){
		return proyectoRepo.findAll();
	}

    @GetMapping("/{id}")
    public Optional<Proyecto> findById(@PathVariable("id") Long id){
        return proyectoRepo.findById(id);
    }

    @PostMapping("/create")
    public Proyecto createProject(@RequestBody Proyecto project){
        Optional<Usuario> user = usuarioRepo.findById(project.getUsuario().getId());
        if(user.isEmpty()){
            return null;
        }else{
            project.setUsuario(user.get());
            return proyectoRepo.save(project);
        }
    }
    
    @PatchMapping("/update/{id}")
    public Proyecto updateProject(@PathVariable("id") Long id, @RequestBody Proyecto project){
        Optional<Proyecto> proyecto = proyectoRepo.findById(id);
        if(proyecto.isEmpty()){
            return null;
        }
        proyecto.get().setNombre(project.getNombre());
        proyecto.get().setDescripcion(project.getDescripcion());
        proyecto.get().setEstado(project.getEstado());
        proyecto.get().setInicio(project.getInicio());
        proyecto.get().setFin(project.getFin());
        return proyectoRepo.save(proyecto.get());
    }

    @DeleteMapping("/delete/{id}")
    public String deleteProject(@PathVariable("id") Long id){
        if(proyectoRepo.findById(id).isEmpty()){
            return "Project not found";
        }else{
            proyectoRepo.deleteById(id);
            return "Project deleted";
        }
    }

    @PatchMapping("/addTag/{id}")
    public Proyecto addTagToProject(@RequestParam("TagID") Long tagId, @PathVariable("id") Long id){
        Optional<Proyecto> project = proyectoRepo.findById(id);
        if(project.isEmpty()){
            return null;
        }else{
            Optional<Etiqueta> tag = etiquetaRepo.findById(tagId);
            if(tag.isEmpty()){
                return null;
            }else{
                List<Etiqueta> tagList = project.get().getEtiquetas();
                if(tagList.contains(tag.get())){
                    return null;
                }
                tagList.add(tag.get());
                project.get().setEtiquetas(tagList);
                return proyectoRepo.save(project.get());
            }
        }
    }

    @PatchMapping("/removeTag/{id}")
    public Proyecto delTagFromProject(@RequestParam("TagID") Long tagId, @PathVariable("id") Long id){
        Optional<Proyecto> project = proyectoRepo.findById(id);
        if(project.isEmpty()){
            return null;
        }else{
            Optional<Etiqueta> tag = etiquetaRepo.findById(tagId);
            if(tag.isEmpty()){
                return null;
            }else{
                List<Etiqueta> tagList = project.get().getEtiquetas();
                if(!tagList.contains(tag.get())){
                    return null;
                }
                tagList.remove(tag.get());
                project.get().setEtiquetas(tagList);
                return proyectoRepo.save(project.get());
            }
        }
    }
}
