package org.khanhpham.wms.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationResponse<T> {
    private List<T> data;
    private int page;
    private int limit;
    private long totalResults;
    private int totalPages;
    private boolean last;
}
