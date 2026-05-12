package com.cyan.dataauth.cmd;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricCheckCmd implements Serializable {

    private static final long serialVersionUID = 1L;

    private String passport;
    private List<CheckItem> checkItems;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckItem implements Serializable {
        private String resourceType;
        private String resourceId;
        private String action;
    }
}
