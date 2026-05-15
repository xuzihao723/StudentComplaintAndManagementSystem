package edu.demo.scfs.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SlaMonitor {
    private final CaseService cases;

    public SlaMonitor(CaseService cases) {
        this.cases = cases;
    }

    @Scheduled(fixedDelayString = "${app.sla-check-delay-ms:300000}")
    public void checkOverdueCases() {
        cases.markOverdueCases();
    }
}
