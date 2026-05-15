package edu.demo.scfs.service;

import edu.demo.scfs.domain.CasePriority;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class SlaPolicyTest {
    @Test
    void urgentPriorityShortensCategorySla() {
        Instant submittedAt = Instant.parse("2026-05-10T00:00:00Z");

        Instant normalDue = SlaPolicy.calculateDueAt(submittedAt, 72, CasePriority.NORMAL);
        Instant urgentDue = SlaPolicy.calculateDueAt(submittedAt, 72, CasePriority.URGENT);

        assertThat(normalDue).isEqualTo(Instant.parse("2026-05-13T00:00:00Z"));
        assertThat(urgentDue).isEqualTo(Instant.parse("2026-05-10T18:00:00Z"));
    }
}

