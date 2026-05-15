package edu.demo.scfs.web.dto;

import edu.demo.scfs.domain.WorkflowTemplate;

public final class ReferenceDtos {
    private ReferenceDtos() {
    }

    public record DepartmentSummary(Long id, String name, String description, boolean enabled) {
    }

    public record CategorySummary(
            Long id,
            String name,
            String description,
            boolean anonymousAllowed,
            boolean enabled,
            Long defaultDepartmentId,
            String defaultDepartmentName,
            int defaultSlaHours,
            WorkflowTemplate workflowTemplate
    ) {
    }
}
