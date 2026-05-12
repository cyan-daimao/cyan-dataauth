package com.cyan.dataauth.adapter.check.convert;

import com.cyan.dataauth.application.check.bo.*;
import com.cyan.dataauth.dto.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 权限校验适配器转换
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Component
public class AuthCheckAdapterConvert {

    public AuthCheckResult toAuthCheckResult(AuthCheckResultBO bo) {
        if (bo == null) {
            return null;
        }
        return new AuthCheckResult(bo.isPermitted(), bo.getReason());
    }

    public FilterSqlResult toFilterSqlResult(FilterSqlResultBO bo) {
        if (bo == null) {
            return null;
        }
        return new FilterSqlResult(bo.isPermitted(), bo.getReason(), bo.getOriginalSql(),
                bo.getRewrittenSql(), null, null);
    }

    public ResourceTreeNode toResourceTreeNode(ResourceTreeNodeBO bo) {
        if (bo == null) {
            return null;
        }
        ResourceTreeNode node = new ResourceTreeNode();
        node.setId(bo.getId());
        node.setName(bo.getName());
        node.setType(bo.getType());
        node.setPermission(bo.getPermission());
        node.setChildren(toResourceTreeNodeList(bo.getChildren()));
        return node;
    }

    public List<ResourceTreeNode> toResourceTreeNodeList(List<ResourceTreeNodeBO> bos) {
        return Optional.ofNullable(bos).orElse(List.of())
                .stream().map(this::toResourceTreeNode).collect(Collectors.toList());
    }

    public MetricCheckResult toMetricCheckResult(MetricCheckResultBO bo) {
        if (bo == null) {
            return null;
        }
        List<MetricCheckItemResult> results = Optional.ofNullable(bo.getResults()).orElse(List.of())
                .stream().map(r -> new MetricCheckItemResult(r.getResourceType(), r.getResourceId(),
                        r.isPermitted(), r.getReason())).collect(Collectors.toList());
        return new MetricCheckResult(bo.isAllPermitted(), results);
    }

    public MetricResourceDTO toMetricResourceDTO(MetricResourceBO bo) {
        if (bo == null) {
            return null;
        }
        MetricResourceDTO dto = new MetricResourceDTO();
        dto.setId(bo.getId());
        dto.setCode(bo.getCode());
        dto.setName(bo.getName());
        dto.setSubjectCode(bo.getSubjectCode());
        dto.setSubjectName(bo.getSubjectName());
        return dto;
    }

    public List<MetricResourceDTO> toMetricResourceDTOList(List<MetricResourceBO> bos) {
        return Optional.ofNullable(bos).orElse(List.of())
                .stream().map(this::toMetricResourceDTO).collect(Collectors.toList());
    }

    public MetricFilterSqlResult toMetricFilterSqlResult(MetricFilterSqlResultBO bo) {
        if (bo == null) {
            return null;
        }
        return new MetricFilterSqlResult(bo.isPermitted(), bo.getReason(), bo.getOriginalSql(),
                bo.getRewrittenSql(), null);
    }
}
