package org.khanhpham.wms.utils;

import lombok.experimental.UtilityClass;
import org.khanhpham.wms.domain.response.PaginationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@UtilityClass
public class PaginationUtils {
    public static <T> PaginationResponse<T> createPaginationResponse(List<T> content, Page<?> page) {
        PaginationResponse<T> response = new PaginationResponse<>();
        response.setData(content);
        response.setLimit(page.getSize());
        response.setPage(page.getNumber() + 1);
        response.setTotalResults(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLast(page.isLast());
        return response;
    }


    public static Pageable convertToPageable(int pageNumber, int pageSize, String sortBy, String sortDir) {
        int adjustedPageNumber = Math.max(pageNumber - 1, 0); // Subtract 1 from pageNumber and ensure it's not negative
        return PageRequest.of(adjustedPageNumber, pageSize,
                Sort.by(sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
    }
}
