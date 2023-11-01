package com.gettasksdone.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.gettasksdone.model.Etiqueta;
import com.gettasksdone.repository.EtiquetaRepository;

@RestController
@RequestMapping("/tag")
public class EtiquetaController {

    @Autowired
    private EtiquetaRepository etiquetaRepo;

    @GetMapping("/getTags")
	public List<Etiqueta> allTags(){
		return etiquetaRepo.findAll();
	}

    @GetMapping("/{id}")
	public Optional<Etiqueta> findById(@PathVariable("id") Long id){
        return etiquetaRepo.findById(id);
	}

    @PostMapping("/createTag")
	public Etiqueta createTag(@RequestBody Etiqueta etiqueta) {
		return etiquetaRepo.save(etiqueta);
	}

    @PatchMapping("/update/{id}")
	public Etiqueta updateTag(@PathVariable int id ,@RequestBody Etiqueta etiqueta) {
		return etiquetaRepo.save(etiqueta);
	}

    @DeleteMapping("/delete/{id}")
	public String deleteTag(@PathVariable("id") Long id) {
        if(etiquetaRepo.findById(id).isEmpty()){
            return "Tag not found";
        }else{
            etiquetaRepo.deleteById(id);
            return "Tag deleted";
        }
	}
}
