package com.gettasksdone.service.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import com.gettaskdone.utils.MHelpers;
import com.gettasksdone.dto.NotaDTO;
import com.gettasksdone.model.Nota;
import com.gettasksdone.repository.NotaRepository;
import com.gettasksdone.service.NotaService;

public class NotaImpl implements NotaService {

    @Autowired
    private NotaRepository notaRepo;

    @Override
    public List<NotaDTO> findAll() {
        List<NotaDTO> notesDTO = new ArrayList<>();
        List<Nota> notes = this.notaRepo.findAll();
        for(Nota note: notes){
            NotaDTO noteDTO = MHelpers.modelMapper().map(note, NotaDTO.class);
            notesDTO.add(noteDTO);
        }
        return notesDTO;
    }

    @Override
    public NotaDTO findById(Long id) {
        Optional<Nota> note = this.notaRepo.findById(id);
        if(note.isEmpty()){
            return null;
        }
        return MHelpers.modelMapper().map(note, NotaDTO.class);
    }
    
}
