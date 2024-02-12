package com.gettasksdone.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	public ResponseEntity<List<Etiqueta>> allTags(){
		return new ResponseEntity<>(etiquetaRepo.findAll(), HttpStatus.OK);
	}

    @GetMapping("/{id}")
	public ResponseEntity<?> findById(@PathVariable("id") Long id){
        Optional<Etiqueta> tag = etiquetaRepo.findById(id);
        if(tag.isEmpty()){
            return new ResponseEntity<>("Tag not found.", HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(tag.get(), HttpStatus.OK);
        }
	}

    @PostMapping("/createTag")
	public ResponseEntity<Etiqueta> createTag(@RequestBody Etiqueta etiqueta) {
		return new ResponseEntity<>(etiquetaRepo.save(etiqueta), HttpStatus.OK);
	}

    @PatchMapping("/update/{id}")
	public ResponseEntity<?> updateTag(@PathVariable("id") Long id ,@RequestBody Etiqueta etiqueta) {
        Optional<Etiqueta> tag = etiquetaRepo.findById(id);
        if(tag.isEmpty()){
            return new ResponseEntity<>("Tag not found.", HttpStatus.BAD_REQUEST);
        }
        tag.get().setNombre(etiqueta.getNombre());
		return new ResponseEntity<>(etiquetaRepo.save(tag.get()), HttpStatus.OK);
	}

    @DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteTag(@PathVariable("id") Long id) {
        if(etiquetaRepo.findById(id).isEmpty()){
            return new ResponseEntity<>("Tag not found", HttpStatus.NOT_FOUND);
        }else{
            etiquetaRepo.deleteById(id);
            return new ResponseEntity<>("Tag deleted", HttpStatus.OK);
        }
	}
}
