package com.cyan.dataauth.cmd;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterSqlCmd implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String passport;
    private String sql;
    private String engine;
}
