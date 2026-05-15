package edu.demo.scfs.service;

import edu.demo.scfs.domain.CasePriority;
import java.time.Instant;

public final class SlaPolicy {
    private SlaPolicy() {
    }

    public static Instant calculateDueAt(Instant submittedAt, int defaultSlaHours, CasePriority priority) {
        int baseHours = defaultSlaHours <= 0 ? 72 : defaultSlaHours;
        CasePriority normalized = priority == null ? CasePriority.NORMAL : priority;
        long hours = switch (normalized) {
            case LOW -> baseHours * 2L;
            case NORMAL -> baseHours;
            case HIGH -> Math.max(1, baseHours / 2L);
            case URGENT -> Math.max(1, baseHours / 4L);
        };
        return submittedAt.plusSeconds(hours * 3600L);
    }
}
