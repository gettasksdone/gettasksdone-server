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
import com.gettasksdone.model.Contexto;
import com.gettasksdone.repository.ContextoRepository;

@RestController
@RequestMapping("/context")
public class ContextoController {

    @Autowired
    private ContextoRepository contextoRepo;

    @GetMapping("/getContexts")
	public List<Contexto> allContexts(){
		return contextoRepo.findAll();
	}

    @GetMapping("/{id}")
	public Optional<Contexto> findById(@PathVariable("id") Long id){
        return contextoRepo.findById(id);
	}

    @PostMapping("/createContext")
	public Contexto createContext(@RequestBody Contexto contexto) {
		return contextoRepo.save(contexto);
	}

    @PatchMapping("/update/{id}")
	public Contexto updateContext(@PathVariable("id") Long id ,@RequestBody Contexto contexto) {
        Optional<Contexto> context = contextoRepo.findById(id);
        if(context.isEmpty()){
            return null;
        }
        context.get().setNombre(contexto.getNombre());
		return contextoRepo.save(context.get());
	}

    @DeleteMapping("/delete/{id}")
	public String deleteContext(@PathVariable("id") Long id) {
        if(contextoRepo.findById(id).isEmpty()){
            return "Context not found";
        }else{
            contextoRepo.deleteById(id);
            return "Context deleted";
        }
	}
}
