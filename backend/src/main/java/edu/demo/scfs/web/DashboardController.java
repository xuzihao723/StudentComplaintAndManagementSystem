package edu.demo.scfs.web;

import edu.demo.scfs.security.UserPrincipal;
import edu.demo.scfs.service.CaseService;
import edu.demo.scfs.web.dto.CaseDtos.DashboardSummary;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final CaseService cases;

    public DashboardController(CaseService cases) {
        this.cases = cases;
    }

    @GetMapping("/summary")
    public DashboardSummary summary(@AuthenticationPrincipal UserPrincipal principal) {
        return cases.dashboardSummary(principal.user());
    }
}
