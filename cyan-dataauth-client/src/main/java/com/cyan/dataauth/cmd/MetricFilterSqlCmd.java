package com.cyan.dataauth.cmd;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricFilterSqlCmd implements Serializable {

    private static final long serialVersionUID = 1L;

    private String passport;
    private String sql;
    private List<String> metricCodes;
    private List<String> dimCodes;
    private String engine;
}
