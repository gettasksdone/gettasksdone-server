package com.gettasksdone.service.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import com.gettaskdone.utils.MHelpers;
import com.gettasksdone.dto.CheckItemDTO;
import com.gettasksdone.model.CheckItem;
import com.gettasksdone.repository.CheckItemRepository;
import com.gettasksdone.service.CheckItemService;

public class CheckItemImpl implements CheckItemService {

    @Autowired
    private CheckItemRepository checkItemRepo;

    @Override
    public CheckItemDTO findById(Long id) {
        Optional<CheckItem> checkItem = this.checkItemRepo.findById(id);
        if(checkItem.isEmpty()){
            return null;
        }
        return MHelpers.modelMapper().map(checkItem, CheckItemDTO.class);
    }

    @Override
    public List<CheckItemDTO> findAll() {
        List<CheckItemDTO> checkItemsDTO = new ArrayList<>();
        List<CheckItem> checkItems = this.checkItemRepo.findAll();
        for(CheckItem checkItem: checkItems){
            CheckItemDTO checkItemDTO = MHelpers.modelMapper().map(checkItem, CheckItemDTO.class);
            checkItemsDTO.add(checkItemDTO);
        }
        return checkItemsDTO;
    }
    
}
