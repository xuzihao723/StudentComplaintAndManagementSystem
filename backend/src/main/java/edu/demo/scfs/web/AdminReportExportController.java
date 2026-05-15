package edu.demo.scfs.web;

import edu.demo.scfs.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/reports")
public class AdminReportExportController {
    private final ReportService reports;

    public AdminReportExportController(ReportService reports) {
        this.reports = reports;
    }

    @PostMapping("/export")
    public ResponseEntity<String> export() {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"scfs-report.csv\"")
                .body(reports.exportCsv());
    }
}
