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
import com.gettasksdone.model.Contexto;
import com.gettasksdone.repository.ContextoRepository;

@RestController
@RequestMapping("/context")
public class ContextoController {

    @Autowired
    private ContextoRepository contextoRepo;

    @GetMapping("/getContexts")
	public ResponseEntity<List<Contexto>> allContexts(){
		return new ResponseEntity<>(contextoRepo.findAll(), HttpStatus.OK);
	}

    @GetMapping("/{id}")
	public ResponseEntity<?> findById(@PathVariable("id") Long id){
        Optional<Contexto> context = contextoRepo.findById(id);
        if(context.isEmpty()){
            return new ResponseEntity<>("Context not found.", HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(context.get(), HttpStatus.OK);
        }
	}

    @PostMapping("/createContext")
	public ResponseEntity<Contexto> createContext(@RequestBody Contexto contexto) {
		return new ResponseEntity<>(contextoRepo.save(contexto), HttpStatus.OK);
	}

    @PatchMapping("/update/{id}")
	public ResponseEntity<?> updateContext(@PathVariable("id") Long id ,@RequestBody Contexto contexto) {
        Optional<Contexto> context = contextoRepo.findById(id);
        if(context.isEmpty()){
            return new ResponseEntity<>("Context not found.", HttpStatus.BAD_REQUEST);
        }
        context.get().setNombre(contexto.getNombre());
		return new ResponseEntity<>(contextoRepo.save(context.get()), HttpStatus.OK);
	}

    @DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteContext(@PathVariable("id") Long id) {
        if(contextoRepo.findById(id).isEmpty()){
            return new ResponseEntity<>("Context not found", HttpStatus.NOT_FOUND);
        }else{
            contextoRepo.deleteById(id);
            return new ResponseEntity<>("Context deleted", HttpStatus.OK);
        }
	}
}
