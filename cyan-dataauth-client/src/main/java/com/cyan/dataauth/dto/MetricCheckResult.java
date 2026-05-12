package com.cyan.dataauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricCheckResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean allPermitted;
    private List<MetricCheckItemResult> results;
}
