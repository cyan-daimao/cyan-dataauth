package com.cyan.dataauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricCheckItemResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String resourceType;
    private String resourceId;
    private boolean permitted;
    private String reason;
}
