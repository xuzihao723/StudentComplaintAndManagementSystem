package edu.demo.scfs.web.dto;

import edu.demo.scfs.domain.WorkflowTemplate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class AdminReferenceDtos {
    private AdminReferenceDtos() {
    }

    public record DepartmentRequest(@NotBlank String name, String description, boolean enabled) {
    }

    public record CategoryRequest(
            @NotBlank String name,
            String description,
            boolean anonymousAllowed,
            boolean enabled,
            @NotNull Long defaultDepartmentId,
            Integer defaultSlaHours,
            WorkflowTemplate workflowTemplate
    ) {
    }
}
