package edu.demo.scfs.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.demo.scfs.domain.AppUser;
import edu.demo.scfs.domain.ComplaintCase;
import edu.demo.scfs.domain.WeeklyReport;
import edu.demo.scfs.repository.ComplaintCaseRepository;
import edu.demo.scfs.repository.WeeklyReportRepository;
import edu.demo.scfs.web.dto.CaseDtos.WeeklyReportSummary;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {
    private final ComplaintCaseRepository cases;
    private final WeeklyReportRepository reports;
    private final ObjectMapper objectMapper;

    public ReportService(ComplaintCaseRepository cases, WeeklyReportRepository reports, ObjectMapper objectMapper) {
        this.cases = cases;
        this.reports = reports;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public WeeklyReportSummary generate(AppUser admin) {
        LocalDate today = LocalDate.now();
        LocalDate start = today.with(DayOfWeek.MONDAY);
        LocalDate end = start.plusDays(6);
        List<ComplaintCase> weekCases = cases.findBySubmittedAtBetween(
                start.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                end.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
        );
        Map<String, Object> stats = statistics(weekCases);
        WeeklyReport report = new WeeklyReport();
        report.setWeekStart(start);
        report.setWeekEnd(end);
        report.setGeneratedBy(admin);
        try {
            report.setStatisticsJson(objectMapper.writeValueAsString(stats));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to serialize report", ex);
        }
        return toSummary(reports.save(report));
    }

    @Transactional(readOnly = true)
    public List<WeeklyReportSummary> list() {
        return reports.findAllByOrderByGeneratedAtDesc().stream().map(this::toSummary).toList();
    }

    private Map<String, Object> statistics(List<ComplaintCase> weekCases) {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalCases", weekCases.size());
        stats.put("statusCounts", weekCases.stream()
                .collect(Collectors.groupingBy(item -> item.getStatus().name(), LinkedHashMap::new, Collectors.counting())));
        stats.put("categoryCounts", weekCases.stream()
                .collect(Collectors.groupingBy(item -> item.getCategory().getName(), LinkedHashMap::new, Collectors.counting())));
        stats.put("departmentCounts", weekCases.stream()
                .filter(item -> item.getAssignedDepartment() != null)
                .collect(Collectors.groupingBy(item -> item.getAssignedDepartment().getName(), LinkedHashMap::new, Collectors.counting())));
        long followUps = weekCases.stream().filter(item -> item.getStatus().name().contains("FOLLOW_UP")).count();
        stats.put("followUpCases", followUps);
        long overdue = weekCases.stream().filter(ComplaintCase::isOverdue).count();
        stats.put("overdueCases", overdue);
        stats.put("overdueRate", weekCases.isEmpty() ? 0 : (double) overdue / weekCases.size());
        stats.put("priorityCounts", weekCases.stream()
                .collect(Collectors.groupingBy(item -> item.getPriority().name(), LinkedHashMap::new, Collectors.counting())));
        double averageSatisfaction = weekCases.stream()
                .filter(item -> item.getSatisfactionRating() != null)
                .mapToInt(ComplaintCase::getSatisfactionRating)
                .average()
                .orElse(0);
        stats.put("averageSatisfaction", averageSatisfaction);
        double averageHours = weekCases.stream()
                .filter(item -> item.getResolvedAt() != null)
                .mapToLong(item -> Duration.between(item.getSubmittedAt(), item.getResolvedAt()).toHours())
                .average()
                .orElse(0);
        stats.put("averageResolutionHours", averageHours);
        return stats;
    }

    @Transactional(readOnly = true)
    public String exportCsv() {
        StringBuilder csv = new StringBuilder("caseNumber,title,status,priority,category,department,submittedAt,dueAt,overdue,satisfactionRating\n");
        for (ComplaintCase item : cases.findAll()) {
            csv.append(escape(item.getCaseNumber())).append(',')
                    .append(escape(item.getTitle())).append(',')
                    .append(item.getStatus()).append(',')
                    .append(item.getPriority()).append(',')
                    .append(escape(item.getCategory().getName())).append(',')
                    .append(escape(item.getAssignedDepartment() == null ? "" : item.getAssignedDepartment().getName())).append(',')
                    .append(item.getSubmittedAt()).append(',')
                    .append(item.getDueAt()).append(',')
                    .append(item.isOverdue()).append(',')
                    .append(item.getSatisfactionRating() == null ? "" : item.getSatisfactionRating())
                    .append('\n');
        }
        return csv.toString();
    }

    private String escape(String value) {
        String safe = value == null ? "" : value;
        return "\"" + safe.replace("\"", "\"\"") + "\"";
    }

    private WeeklyReportSummary toSummary(WeeklyReport report) {
        try {
            Map<String, Object> stats = objectMapper.readValue(report.getStatisticsJson(), new TypeReference<>() {
            });
            return new WeeklyReportSummary(
                    report.getId(),
                    report.getWeekStart(),
                    report.getWeekEnd(),
                    stats,
                    report.getGeneratedBy().getFullName(),
                    report.getGeneratedAt()
            );
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to read report", ex);
        }
    }
}
