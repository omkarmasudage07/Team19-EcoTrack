package com.ecotrack.material.service.impl;

import com.ecotrack.material.dto.request.MaterialRequest;
import com.ecotrack.material.dto.response.MaterialResponse;
import com.ecotrack.material.entity.Material;
import com.ecotrack.material.entity.MaterialCategory;
import com.ecotrack.material.entity.MaterialImage;
import com.ecotrack.material.enums.AvailabilityStatus;
import com.ecotrack.material.exception.BusinessException;
import com.ecotrack.material.exception.ResourceNotFoundException;
import com.ecotrack.material.mapper.MaterialMapper;
import com.ecotrack.material.repository.MaterialCategoryRepository;
import com.ecotrack.material.repository.MaterialImageRepository;
import com.ecotrack.material.repository.MaterialRepository;
import com.ecotrack.material.service.MaterialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository materialRepository;
    private final MaterialImageRepository materialImageRepository;
    private final MaterialCategoryRepository categoryRepository;

    @Override
    @Transactional
    public MaterialResponse createMaterial(Long recyclerId, MaterialRequest request) {
        MaterialCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Material category not found"));

        Material material = Material.builder()
                .recyclerId(recyclerId)
                .category(category)
                .materialName(request.getMaterialName())
                .description(request.getDescription())
                .purity(request.getPurity())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
                .pricePerUnit(request.getPricePerUnit())
                .warehouseLocation(request.getWarehouseLocation())
                .availabilityStatus(resolveAvailability(request.getQuantity()))
                .build();

        material = materialRepository.save(material);
        saveImages(material, request.getImageUrls());

        log.info("Material '{}' listed by recycler {}", material.getMaterialName(), recyclerId);
        return getMaterialDetail(material.getId());
    }

    @Override
    @Transactional
    public MaterialResponse updateMaterial(Long materialId, Long recyclerId, MaterialRequest request) {
        Material material = findOwnedMaterialOrThrow(materialId, recyclerId);

        MaterialCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Material category not found"));

        material.setCategory(category);
        material.setMaterialName(request.getMaterialName());
        material.setDescription(request.getDescription());
        material.setPurity(request.getPurity());
        material.setQuantity(request.getQuantity());
        material.setUnit(request.getUnit());
        material.setPricePerUnit(request.getPricePerUnit());
        material.setWarehouseLocation(request.getWarehouseLocation());
        material.setAvailabilityStatus(resolveAvailability(request.getQuantity()));
        material = materialRepository.save(material);

        if (request.getImageUrls() != null) {
            materialImageRepository.deleteAll(materialImageRepository.findByMaterialId(materialId));
            saveImages(material, request.getImageUrls());
        }

        return getMaterialDetail(material.getId());
    }

    @Override
    @Transactional
    public void deleteMaterial(Long materialId, Long recyclerId) {
        Material material = findOwnedMaterialOrThrow(materialId, recyclerId);
        materialImageRepository.deleteAll(materialImageRepository.findByMaterialId(materialId));
        materialRepository.delete(material);
        log.info("Material {} deleted by recycler {}", materialId, recyclerId);
    }

    @Override
    public MaterialResponse getMaterialDetail(Long materialId) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found with id: " + materialId));
        List<String> imageUrls = materialImageRepository.findByMaterialId(materialId).stream()
                .map(MaterialImage::getImageUrl)
                .collect(Collectors.toList());
        return MaterialMapper.toResponse(material, imageUrls);
    }

    @Override
    public Page<MaterialResponse> getRecyclerMaterials(Long recyclerId, Pageable pageable) {
        return materialRepository.findByRecyclerId(recyclerId, pageable)
                .map(material -> MaterialMapper.toResponse(material, Collections.emptyList()));
    }

    @Override
    public Page<MaterialResponse> browseMarketplace(String materialName, Long categoryId, Pageable pageable) {
        Page<Material> materials;
        if (materialName != null && !materialName.isBlank()) {
            materials = materialRepository.findByMaterialNameContainingIgnoreCaseAndAvailabilityStatus(
                    materialName, AvailabilityStatus.AVAILABLE, pageable);
        } else if (categoryId != null) {
            materials = materialRepository.findByCategoryIdAndAvailabilityStatus(
                    categoryId, AvailabilityStatus.AVAILABLE, pageable);
        } else {
            materials = materialRepository.findByAvailabilityStatus(AvailabilityStatus.AVAILABLE, pageable);
        }
        return materials.map(material -> MaterialMapper.toResponse(material, Collections.emptyList()));
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private Material findOwnedMaterialOrThrow(Long materialId, Long recyclerId) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found with id: " + materialId));
        if (!material.getRecyclerId().equals(recyclerId)) {
            throw new BusinessException("You can only manage your own material listings", HttpStatus.FORBIDDEN);
        }
        return material;
    }

    private AvailabilityStatus resolveAvailability(BigDecimal quantity) {
        return quantity.compareTo(BigDecimal.ZERO) > 0 ? AvailabilityStatus.AVAILABLE : AvailabilityStatus.OUT_OF_STOCK;
    }

    private void saveImages(Material material, List<String> imageUrls) {
        if (imageUrls == null) {
            return;
        }
        for (String url : imageUrls) {
            materialImageRepository.save(MaterialImage.builder().material(material).imageUrl(url).build());
        }
    }
}
