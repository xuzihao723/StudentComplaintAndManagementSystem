package edu.demo.scfs.web;

import edu.demo.scfs.security.UserPrincipal;
import edu.demo.scfs.service.CaseService;
import edu.demo.scfs.web.dto.CaseDtos.CaseDetail;
import edu.demo.scfs.web.dto.CaseDtos.CaseSummary;
import edu.demo.scfs.web.dto.CaseDtos.TextRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/department/cases")
public class DepartmentCaseController {
    private final CaseService cases;

    public DepartmentCaseController(CaseService cases) {
        this.cases = cases;
    }

    @GetMapping
    public List<CaseSummary> list(@AuthenticationPrincipal UserPrincipal principal) {
        return cases.departmentCases(principal.user());
    }

    @GetMapping("/{id}")
    public CaseDetail detail(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return cases.departmentCase(principal.user(), id);
    }

    @PostMapping("/{id}/progress")
    public CaseDetail progress(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody TextRequest request
    ) {
        return cases.updateProgress(principal.user(), id, request);
    }

    @PostMapping("/{id}/resolve")
    public CaseDetail resolve(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody TextRequest request
    ) {
        return cases.resolveCase(principal.user(), id, request);
    }
}

