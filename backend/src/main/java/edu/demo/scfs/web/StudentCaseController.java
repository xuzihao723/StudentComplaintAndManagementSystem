package edu.demo.scfs.web;

import edu.demo.scfs.security.UserPrincipal;
import edu.demo.scfs.domain.CasePriority;
import edu.demo.scfs.service.CaseService;
import edu.demo.scfs.web.dto.CaseDtos.CaseDetail;
import edu.demo.scfs.web.dto.CaseDtos.CaseSummary;
import edu.demo.scfs.web.dto.CaseDtos.SatisfactionRequest;
import edu.demo.scfs.web.dto.CaseDtos.TextRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/student/cases")
public class StudentCaseController {
    private final CaseService cases;

    public StudentCaseController(CaseService cases) {
        this.cases = cases;
    }

    @GetMapping
    public List<CaseSummary> list(@AuthenticationPrincipal UserPrincipal principal) {
        return cases.studentCases(principal.user());
    }

    @GetMapping("/{id}")
    public CaseDetail detail(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return cases.studentCase(principal.user(), id);
    }

    @PostMapping
    public CaseDetail submit(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam Long categoryId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(defaultValue = "false") boolean anonymous,
            @RequestParam(required = false) CasePriority priority,
            @RequestPart(required = false) List<MultipartFile> files
    ) {
        return cases.submitCase(principal.user(), categoryId, title, description, anonymous, priority, files);
    }

    @PostMapping("/{id}/messages")
    public CaseDetail addMessage(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @org.springframework.web.bind.annotation.RequestBody TextRequest request
    ) {
        return cases.addStudentMessage(principal.user(), id, request);
    }

    @PostMapping("/{id}/follow-up")
    public CaseDetail followUp(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @org.springframework.web.bind.annotation.RequestBody TextRequest request
    ) {
        return cases.requestFollowUp(principal.user(), id, request);
    }

    @PostMapping("/{id}/reopen")
    public CaseDetail reopen(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @org.springframework.web.bind.annotation.RequestBody TextRequest request
    ) {
        return cases.requestReopen(principal.user(), id, request);
    }

    @PostMapping("/{id}/satisfaction")
    public CaseDetail satisfaction(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @org.springframework.web.bind.annotation.RequestBody SatisfactionRequest request
    ) {
        return cases.submitSatisfaction(principal.user(), id, request);
    }
}
