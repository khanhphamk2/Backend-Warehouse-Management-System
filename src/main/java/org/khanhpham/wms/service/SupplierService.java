package org.khanhpham.wms.service;

import org.khanhpham.wms.domain.dto.SupplierDTO;
import org.khanhpham.wms.domain.entity.Supplier;
import org.khanhpham.wms.domain.request.SupplierRequest;
import org.khanhpham.wms.domain.response.PaginationResponse;

public interface SupplierService {
    SupplierDTO createSupplier(SupplierRequest request);
    SupplierDTO updateSupplier(Long id, SupplierRequest request);
    SupplierDTO getSupplier(Long id);
    void deleteSupplier(Long id);
    PaginationResponse<SupplierDTO> getAllSuppliers(int pageNumber, int pageSize, String sortBy, String sortDir);
    Supplier getSupplierById(Long id);
}
