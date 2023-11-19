package com.gettasksdone.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.gettasksdone.model.CheckItem;
import com.gettasksdone.model.Nota;
import com.gettasksdone.repository.CheckItemRepository;
import com.gettasksdone.repository.NotaRepository;


@RestController
@RequestMapping("/api")
public class RootController {
    @Autowired
    private NotaRepository notaRepo;
    @Autowired
    private CheckItemRepository checkRepo;

    @GetMapping("/test")
    public String prueba(){
        return "El API funciona";
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