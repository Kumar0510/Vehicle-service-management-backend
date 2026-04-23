package com.autotrack.inventoryservice.config;

import com.autotrack.inventoryservice.dto.SparePartRequestDTO;
import com.autotrack.inventoryservice.dto.SparePartResponseDTO;
import com.autotrack.inventoryservice.model.SparePart;
import org.springframework.stereotype.Component;

@Component
public class SparePartMapper {

    public SparePart toEntity(SparePartRequestDTO dto){
        if(dto == null) return null;

        SparePart entity = new SparePart();

        entity.setName(dto.getName());
        entity.setCost(dto.getCost());
        entity.setQuantityAvailable(dto.getQuantityAvailable());
        entity.setReorderLevel(dto.getReorderLevel());
//        entity.setStandardReorderAmount(dto.getStandardReorderAmount());
        return entity;
    }

    public SparePartResponseDTO toDto(SparePart entity){
        if(entity == null) return null;

        SparePartResponseDTO res = new SparePartResponseDTO();

        res.setPartId(entity.getPartId());
        res.setCost(entity.getCost());
        res.setName(entity.getName());
        res.setReorderLevel(entity.getReorderLevel());
        res.setQuantityAvailable(entity.getQuantityAvailable());
//        res.setStandardReorderAmount(entity.getStandardReorderAmount());
        return res;
    }
}
