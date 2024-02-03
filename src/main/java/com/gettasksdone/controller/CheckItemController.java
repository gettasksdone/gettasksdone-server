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
import com.gettasksdone.model.CheckItem;
import com.gettasksdone.model.Tarea;
import com.gettasksdone.repository.CheckItemRepository;
import com.gettasksdone.repository.TareaRepository;

@RestController
@RequestMapping("/check")
public class CheckItemController {
    @Autowired
    private CheckItemRepository checkRepo;
    @Autowired
    private TareaRepository tareaRepo;

    @GetMapping("/getChecks")
	public List<CheckItem> allChecks(){
		return checkRepo.findAll();
	}

    @GetMapping("/{id}")
    public Optional<CheckItem> findById(@PathVariable("id") Long id){
        return checkRepo.findById(id);
    }

    @PostMapping("/create")
    public CheckItem createCheck(@RequestBody CheckItem check, @RequestParam("TaskID") Long id){
        CheckItem cItem;
        List<CheckItem> items;
        Optional<Tarea> task = tareaRepo.findById(id);
        if(task.isEmpty()){
            return null;
        }else{
            cItem = checkRepo.save(check);
            items = task.get().getCheckItems();
            items.add(cItem);
            task.get().setCheckItems(items);
            tareaRepo.save(task.get());
            return cItem;
        }
    }

    @PatchMapping("/update/{id}")
    public CheckItem updateCheck(@RequestBody CheckItem check, @PathVariable("id") Long id){
        Optional<CheckItem> checkItem = checkRepo.findById(id);
        if(checkItem.isEmpty()){
            return null;
        }
        checkItem.get().setContenido(check.getContenido());
        checkItem.get().setEsta_marcado(check.isEsta_marcado());
        return checkRepo.save(checkItem.get());
    }

    @DeleteMapping("/delete/{id}")
    public String deleteCheck(@PathVariable("id") Long id){
        if(checkRepo.findById(id).isEmpty()){
            return "Check item not found";
        }else{
            checkRepo.deleteById(id);
            return "Check Item deleted";
        }
    }
}
