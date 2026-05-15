package edu.demo.scfs.service;

import edu.demo.scfs.domain.AppUser;
import edu.demo.scfs.domain.CaseAssignment;
import edu.demo.scfs.domain.CaseAttachment;
import edu.demo.scfs.domain.CaseMessage;
import edu.demo.scfs.domain.CaseStatusLog;
import edu.demo.scfs.domain.ComplaintCase;
import edu.demo.scfs.web.dto.CaseDtos.AttachmentSummary;
import edu.demo.scfs.web.dto.CaseDtos.AssignmentSummary;
import edu.demo.scfs.web.dto.CaseDtos.CaseDetail;
import edu.demo.scfs.web.dto.CaseDtos.CaseSummary;
import edu.demo.scfs.web.dto.CaseDtos.MessageSummary;
import edu.demo.scfs.web.dto.CaseDtos.StatusLogSummary;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CaseMapper {
    public CaseSummary toSummary(ComplaintCase item, boolean revealStudent) {
        return new CaseSummary(
                item.getId(),
                item.getCaseNumber(),
                item.getTitle(),
                item.getCategory().getName(),
                displayName(item, revealStudent),
                item.isAnonymous(),
                item.getStatus(),
                item.getPriority(),
                item.getDueAt(),
                item.isOverdue(),
                item.getAssignedDepartment() == null ? null : item.getAssignedDepartment().getName(),
                item.getSubmittedAt(),
                item.getUpdatedAt()
        );
    }

    public CaseDetail toDetail(
            ComplaintCase item,
            List<CaseAttachment> attachments,
            List<CaseMessage> messages,
            List<CaseStatusLog> logs,
            List<CaseAssignment> assignments,
            boolean revealStudent
    ) {
        AppUser student = item.getSubmittedBy();
        return new CaseDetail(
                item.getId(),
                item.getCaseNumber(),
                item.getTitle(),
                item.getDescription(),
                item.getCategory().getName(),
                item.getCategory().getId(),
                displayName(item, revealStudent),
                item.isAnonymous() && !revealStudent ? null : contactEmail(item, student),
                item.isAnonymous(),
                item.getStatus(),
                item.getPriority(),
                item.getWorkflowTemplate(),
                item.getDueAt(),
                item.isOverdue(),
                item.getEscalatedAt(),
                item.getReopenedAt(),
                item.getSatisfactionRating(),
                item.getSatisfactionComment(),
                item.getAssignedDepartment() == null ? null : item.getAssignedDepartment().getId(),
                item.getAssignedDepartment() == null ? null : item.getAssignedDepartment().getName(),
                assignments.stream().map(this::assignment).toList(),
                item.getSubmittedAt(),
                item.getUpdatedAt(),
                item.getResolvedAt(),
                item.getClosedAt(),
                attachments.stream().map(this::attachment).toList(),
                messages.stream().map(this::message).toList(),
                logs.stream().map(this::log).toList()
        );
    }

    private AttachmentSummary attachment(CaseAttachment item) {
        return new AttachmentSummary(
                item.getId(),
                item.getOriginalFileName(),
                item.getContentType(),
                item.getSizeBytes(),
                item.getVirusScanStatus(),
                previewable(item),
                item.getUploadedAt()
        );
    }

    private MessageSummary message(CaseMessage item) {
        AppUser sender = item.getSender();
        return new MessageSummary(
                item.getId(),
                sender == null ? item.getSenderDisplayName() : sender.getFullName(),
                sender == null ? item.getSenderRole() : sender.getRole().name(),
                item.getContent(),
                item.getType(),
                item.getCreatedAt()
        );
    }

    private StatusLogSummary log(CaseStatusLog item) {
        return new StatusLogSummary(
                item.getId(),
                item.getOldStatus(),
                item.getNewStatus(),
                item.getOperator() == null ? "System" : item.getOperator().getFullName(),
                item.getNote(),
                item.getCreatedAt()
        );
    }

    private AssignmentSummary assignment(CaseAssignment item) {
        return new AssignmentSummary(
                item.getId(),
                item.getDepartment().getId(),
                item.getDepartment().getName(),
                item.getAssignmentRole().name(),
                item.getAssignedAt()
        );
    }

    private String displayName(ComplaintCase item, boolean revealStudent) {
        if (item.isAnonymous() && !revealStudent) {
            return item.getSubmittedBy() == null ? "Anonymous public submitter" : "Anonymous student";
        }
        if (item.getSubmittedBy() == null) {
            return item.getPublicSubmitterType() == null ? "Public submitter" : item.getPublicSubmitterType();
        }
        return item.getSubmittedBy().getFullName();
    }

    private String contactEmail(ComplaintCase item, AppUser student) {
        if (student != null) {
            return student.getEmail();
        }
        return item.getPublicContactEmail();
    }

    private boolean previewable(CaseAttachment item) {
        return item.getContentType() != null
                && (item.getContentType().startsWith("image/") || item.getContentType().equals("application/pdf"));
    }
}
