package edu.demo.scfs.web;

import edu.demo.scfs.security.UserPrincipal;
import edu.demo.scfs.service.ReportService;
import edu.demo.scfs.web.dto.CaseDtos.WeeklyReportSummary;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/reports/weekly")
public class AdminReportController {
    private final ReportService reports;

    public AdminReportController(ReportService reports) {
        this.reports = reports;
    }

    @GetMapping
    public List<WeeklyReportSummary> list() {
        return reports.list();
    }

    @PostMapping("/generate")
    public WeeklyReportSummary generate(@AuthenticationPrincipal UserPrincipal principal) {
        return reports.generate(principal.user());
    }

    @PostMapping("/export")
    public ResponseEntity<String> export() {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"scfs-report.csv\"")
                .body(reports.exportCsv());
    }
}
