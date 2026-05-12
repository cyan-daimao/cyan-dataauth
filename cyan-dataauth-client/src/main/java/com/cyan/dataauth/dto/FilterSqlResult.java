package com.cyan.dataauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterSqlResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean permitted;
    private String reason;
    private String originalSql;
    private String rewrittenSql;
    private List<String> rowFilters;
    private List<ColumnMask> columnMasks;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColumnMask implements Serializable {
        private String column;
        private String maskType;
    }
}
