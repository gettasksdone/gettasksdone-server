package com.gettasksdone.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import com.gettasksdone.model.CheckItem;
import com.gettasksdone.model.Tarea;
import com.gettasksdone.model.Usuario;
import com.gettasksdone.repository.CheckItemRepository;
import com.gettasksdone.repository.TareaRepository;
import com.gettasksdone.repository.UsuarioRepository;
import com.gettasksdone.service.CheckItemService;
import com.gettasksdone.utils.MHelpers;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/check")
public class CheckItemController {
    @Autowired
    private CheckItemRepository checkRepo;
    @Autowired
    private TareaRepository tareaRepo;
    @Autowired
    private UsuarioRepository usuarioRepo;
    @Autowired
    private CheckItemService checkService;

    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @GetMapping("/getChecks")
	public ResponseEntity<?> allChecks(){
		return new ResponseEntity<>(checkService.findAll(), HttpStatus.OK);
	}

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id){
        Optional<CheckItem> check = checkRepo.findById(id);
        if(check.isEmpty()){
            return new ResponseEntity<>("Check Item not found.", HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(check.get(), HttpStatus.OK);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createCheck(@RequestBody CheckItem check, @RequestParam("TaskID") Long id, HttpServletRequest request){
        CheckItem cItem;
        List<CheckItem> items;
        Optional<Tarea> task = tareaRepo.findById(id);
        if(task.isEmpty()){
            return new ResponseEntity<>("Task not found.", HttpStatus.BAD_REQUEST);
        }else{
            Long ownerID = task.get().getUsuario().getId();
            Usuario authedUser = usuarioRepo.findById(MHelpers.getIdToken(request)).get();
            if(MHelpers.checkAccess(ownerID, authedUser)){
                cItem = checkRepo.save(check);
                items = task.get().getCheckItems();
                items.add(cItem);
                task.get().setCheckItems(items);
                tareaRepo.save(task.get());
                return new ResponseEntity<>("Check item created.", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Task not found.", HttpStatus.BAD_REQUEST);
            }
        }
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateCheck(@RequestBody CheckItem check, @PathVariable("id") Long id){
        Optional<CheckItem> checkItem = checkRepo.findById(id);
        if(checkItem.isEmpty()){
            return new ResponseEntity<>("Check Item not found.", HttpStatus.BAD_REQUEST);
        }
        checkItem.get().setContenido(check.getContenido());
        checkItem.get().setEsta_marcado(check.isEsta_marcado());
        return new ResponseEntity<>(checkRepo.save(checkItem.get()), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCheck(@PathVariable("id") Long id){
        if(checkRepo.findById(id).isEmpty()){
            return new ResponseEntity<>("Check item not found", HttpStatus.NOT_FOUND);
        }else{
            checkRepo.deleteById(id);
            return new ResponseEntity<>("Check Item deleted", HttpStatus.OK);
        }
    }
}
