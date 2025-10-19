package com.skincare.service;

import com.skincare.dto.response.DropDownResponse;
import com.skincare.repository.SkinTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SkinTypeService {

    private final SkinTypeRepository skinTypeRepository;

    public SkinTypeService(SkinTypeRepository skinTypeRepository) {
        this.skinTypeRepository = skinTypeRepository;
    }


    @Transactional(readOnly = true)
    public List<DropDownResponse> getSkinType() {
        try{
            return skinTypeRepository.findAll().stream()
                    .map(skinType -> DropDownResponse.builder()
                            .id(skinType.getSkinTypeId())
                            .name(skinType.getSkinTypeName())
                            .build())
                    .toList();
        } catch (Exception e ){
            log.error("Get skin type failed", e);
            throw new RuntimeException("Get skin type failed");
        }
    }
}
