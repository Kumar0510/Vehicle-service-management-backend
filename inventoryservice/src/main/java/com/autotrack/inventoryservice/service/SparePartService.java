package com.autotrack.inventoryservice.service;

import com.autotrack.inventoryservice.config.SparePartMapper;
import com.autotrack.inventoryservice.dto.SparePartRequestDTO;
import com.autotrack.inventoryservice.dto.SparePartResponseDTO;
import com.autotrack.inventoryservice.exception.SparePartException;
import com.autotrack.inventoryservice.model.SparePart;
import com.autotrack.inventoryservice.repository.SparePartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SparePartService {

    private SparePartRepository repo;
    private SparePartMapper mapper;

    @Autowired
    public void setRepo(SparePartRepository repo) {
        this.repo = repo;
    }

    @Autowired
    public void setMapper(SparePartMapper mapper){
        this.mapper = mapper;
    }

    public SparePartRepository getRepo() {
        return repo;
    }

    // changing to DTO
    public List<SparePartResponseDTO> getAllSpareParts() {
        List<SparePart> listOfSpareParts = repo.findAll();
        return listOfSpareParts.stream()
                .map(s -> mapper.toDto(s))
                .collect(Collectors.toList());
    }

    // changing to DTO
    public SparePartResponseDTO getSparePartById(long id) {
        SparePart res = repo.findById(id)
                .orElseThrow(() -> new SparePartException("Cannot fetch SparePart of Id : " + id));
        return mapper.toDto(res);
    }

    // changing to DTo
    public SparePartResponseDTO addSparePart(SparePartRequestDTO newSparePart) {
        SparePart s = mapper.toEntity(newSparePart);
        SparePart res =  repo.save(s);
        return mapper.toDto(res);
    }

    //changing to DTO : only the args
    public void updateSparePartById(long id, SparePartRequestDTO updatedPart) {
        repo.findById(id).map(oldPart -> {
            oldPart.setCost(updatedPart.getCost());
            oldPart.setName(updatedPart.getName());
            oldPart.setReorderLevel(updatedPart.getReorderLevel());
            oldPart.setQuantityAvailable(updatedPart.getQuantityAvailable());

            // added new attribute
//            oldPart.setStandardReorderAmount(updatedPart.getStandardReorderAmount());
            return repo.save(oldPart);
        }).orElseThrow(() -> new SparePartException("Cannot update, SparePart ID not found : " + id));
    }

    public void deleteSparePartById(long id) {
        if (!repo.existsById(id)) {
            throw new SparePartException("Cannot delete. Spare Part not found with id: " + id);
        }
        repo.deleteById(id);
    }

    @Transactional
    public void deductStock(long id, int quantityUsed) {

        int rowsModifed = repo.safeDeductStock(id, quantityUsed);
        if(rowsModifed == 0){
            repo.findById(id)
                    .orElseThrow(() ->
                            new SparePartException("Cannot deduct stock, Spare part with id "+ id + " not found in database"));

            throw new SparePartException("Cannot deduct stock, Insufficient Stock to deduct");
        }
    }

    // reorder stock levels code
    //send sid, and quantity
    @Transactional
    public void reorderStockLevels(long sid, int quantity){
        int rowsModified = repo.safeReorderStock(sid, quantity);
        if(rowsModified == 0){
            throw new SparePartException("Cannot restock, Spare Part with ID : " + sid + " not found");
        }
    }

    //TODO : /low-stock custom query

    //changing to DTO
    public List<SparePartResponseDTO> getAllLowStockParts(){
        List<SparePart> listOfLowStock =  repo.getAllLowStockParts();

        return listOfLowStock
                .stream()
                .map(s -> mapper.toDto(s))
                .collect(Collectors.toList());
    }

    public List<SparePartResponseDTO> getBulkSpareParts(List<Long> parts) {
        List<SparePart> partsList =  repo.findByPartIdIn(parts);
        List<SparePartResponseDTO> responseDTOS = partsList.stream().map(mapper::toDto).toList();

        return responseDTOS;
    }
}
