package com.gettasksdone.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.gettasksdone.model.CheckItem;
import com.gettasksdone.repository.CheckItemRepository;


@RestController
@RequestMapping("/api")
public class RootController {
    @Autowired
    private CheckItemRepository checkRepo;

    @GetMapping("/test")
    public String prueba(){
        return "El API funciona";
    }


    @GetMapping("/checks")
	public List<CheckItem> allChecks(){
		return checkRepo.findAll();
	}
}