package com.ecotrack.material.service;

import com.ecotrack.material.dto.request.MaterialRequest;
import com.ecotrack.material.dto.response.MaterialResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MaterialService {

    MaterialResponse createMaterial(Long recyclerId, MaterialRequest request);

    MaterialResponse updateMaterial(Long materialId, Long recyclerId, MaterialRequest request);

    void deleteMaterial(Long materialId, Long recyclerId);

    MaterialResponse getMaterialDetail(Long materialId);

    Page<MaterialResponse> getRecyclerMaterials(Long recyclerId, Pageable pageable);

    /** The public marketplace browse/search that Industrial Buyers use. */
    Page<MaterialResponse> browseMarketplace(String materialName, Long categoryId, Pageable pageable);
}
