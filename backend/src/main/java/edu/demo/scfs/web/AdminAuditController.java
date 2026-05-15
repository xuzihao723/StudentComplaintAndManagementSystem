package edu.demo.scfs.web;

import edu.demo.scfs.service.CaseService;
import edu.demo.scfs.web.dto.CaseDtos.AuditLogSummary;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/audit-logs")
public class AdminAuditController {
    private final CaseService cases;

    public AdminAuditController(CaseService cases) {
        this.cases = cases;
    }

    @GetMapping
    public List<AuditLogSummary> list() {
        return cases.auditLogSummaries();
    }
}
