package com.gettasksdone.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.gettasksdone.model.CheckItem;
import com.gettasksdone.model.InfoUsuario;
import com.gettasksdone.model.Nota;
import com.gettasksdone.model.Proyecto;
import com.gettasksdone.model.Tarea;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.repository.CheckItemRepository;
import com.gettasksdone.repository.InfoUsuarioRepository;
import com.gettasksdone.repository.NotaRepository;
import com.gettasksdone.repository.ProyectoRepository;
import com.gettasksdone.repository.TareaRepository;
import com.gettasksdone.repository.UsuarioRepository;


@RestController
@RequestMapping("/api")
public class RootController {
    //Cambio para la rama nueva
    @Autowired
	private UsuarioRepository usuarioRepo;
    @Autowired
    private TareaRepository tareaRepo;
    @Autowired
    private ProyectoRepository proyectoRepo;
    @Autowired
    private NotaRepository notaRepo;
    @Autowired
    private InfoUsuarioRepository infoUsuarioRepo;
    @Autowired
    private CheckItemRepository checkRepo;

    @GetMapping("/test")
    public String prueba(){
        return "El API funciona";
    }

    @GetMapping("/usersData")
	public List<InfoUsuario> allUsersData(){
		return infoUsuarioRepo.findAll();
	}

    @GetMapping("/users")
	public List<Usuario> allUsers(){
		return usuarioRepo.findAll();
	}

    @GetMapping("/tasks")
	public List<Tarea> allTasks(){
		return tareaRepo.findAll();
	}

    @GetMapping("/projects")
	public List<Proyecto> allProjects(){
		return proyectoRepo.findAll();
	}

    @GetMapping("/notes")
	public List<Nota> allNotes(){
		return notaRepo.findAll();
	}

    @GetMapping("/checks")
	public List<CheckItem> allChecks(){
		return checkRepo.findAll();
	}
}