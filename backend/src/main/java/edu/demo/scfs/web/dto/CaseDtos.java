package edu.demo.scfs.web.dto;

import edu.demo.scfs.domain.CaseStatus;
import edu.demo.scfs.domain.CasePriority;
import edu.demo.scfs.domain.EmailStatus;
import edu.demo.scfs.domain.MessageType;
import edu.demo.scfs.domain.VirusScanStatus;
import edu.demo.scfs.domain.WorkflowTemplate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public final class CaseDtos {
    private CaseDtos() {
    }

    public record CaseSummary(
            Long id,
            String caseNumber,
            String title,
            String categoryName,
            String submittedByName,
            boolean anonymous,
            CaseStatus status,
            CasePriority priority,
            Instant dueAt,
            boolean overdue,
            String assignedDepartmentName,
            Instant submittedAt,
            Instant updatedAt
    ) {
    }

    public record CaseDetail(
            Long id,
            String caseNumber,
            String title,
            String description,
            String categoryName,
            Long categoryId,
            String submittedByName,
            String submittedByEmail,
            boolean anonymous,
            CaseStatus status,
            CasePriority priority,
            WorkflowTemplate workflowTemplate,
            Instant dueAt,
            boolean overdue,
            Instant escalatedAt,
            Instant reopenedAt,
            Integer satisfactionRating,
            String satisfactionComment,
            Long assignedDepartmentId,
            String assignedDepartmentName,
            List<AssignmentSummary> assignments,
            Instant submittedAt,
            Instant updatedAt,
            Instant resolvedAt,
            Instant closedAt,
            List<AttachmentSummary> attachments,
            List<MessageSummary> messages,
            List<StatusLogSummary> statusLogs
    ) {
    }

    public record AttachmentSummary(
            Long id,
            String originalFileName,
            String contentType,
            long sizeBytes,
            VirusScanStatus virusScanStatus,
            boolean previewable,
            Instant uploadedAt
    ) {
    }

    public record MessageSummary(Long id, String senderName, String senderRole, String content, MessageType type, Instant createdAt) {
    }

    public record StatusLogSummary(Long id, CaseStatus oldStatus, CaseStatus newStatus, String operatorName, String note, Instant createdAt) {
    }

    public record AssignmentSummary(Long id, Long departmentId, String departmentName, String assignmentRole, Instant assignedAt) {
    }

    public record PrivateNoteSummary(Long id, String authorName, String content, String rootCause, Instant createdAt) {
    }

    public record AuditLogSummary(Long id, String actorName, String action, String targetType, String targetId, String detail, Instant createdAt) {
    }

    public record TextRequest(@NotBlank String content) {
    }

    public record AssignRequest(Long departmentId, List<Long> departmentIds, String note) {
    }

    public record PublicCaseRequest(
            @NotNull Long categoryId,
            @NotBlank String title,
            @NotBlank String description,
            String publicContactEmail,
            String publicSubmitterType,
            CasePriority priority
    ) {
    }

    public record PublicCaseResponse(CaseDetail detail, String trackingCode) {
    }

    public record TrackCaseRequest(@NotBlank String caseNumber, @NotBlank String trackingCode) {
    }

    public record PublicMessageRequest(@NotBlank String caseNumber, @NotBlank String trackingCode, @NotBlank String content) {
    }

    public record SatisfactionRequest(@Min(1) @Max(5) int rating, String comment) {
    }

    public record SearchFilters(
            String keyword,
            CaseStatus status,
            Long categoryId,
            Long departmentId,
            Boolean anonymous,
            CasePriority priority,
            Boolean overdue
    ) {
    }

    public record ReminderRequest(String note) {
    }

    public record PrivateNoteRequest(@NotBlank String content, String rootCause) {
    }

    public record NotificationSummary(
            Long id,
            String title,
            String content,
            Long caseId,
            String caseNumber,
            String actionType,
            EmailStatus emailStatus,
            String failureReason,
            boolean readFlag,
            Instant createdAt
    ) {
    }

    public record WeeklyReportSummary(
            Long id,
            LocalDate weekStart,
            LocalDate weekEnd,
            Map<String, Object> statistics,
            String generatedBy,
            Instant generatedAt
    ) {
    }

    public record DashboardSummary(
            long total,
            long open,
            long overdue,
            long highPriority,
            long resolved,
            long followUps,
            List<CaseSummary> recentCases,
            List<CaseSummary> overdueCases
    ) {
    }
}
