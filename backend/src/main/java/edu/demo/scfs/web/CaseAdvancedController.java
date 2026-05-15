package edu.demo.scfs.web;

import edu.demo.scfs.domain.CaseAttachment;
import edu.demo.scfs.domain.CasePriority;
import edu.demo.scfs.domain.CaseStatus;
import edu.demo.scfs.repository.CaseAttachmentRepository;
import edu.demo.scfs.security.UserPrincipal;
import edu.demo.scfs.service.CaseService;
import edu.demo.scfs.service.FileStorageService;
import edu.demo.scfs.web.dto.CaseDtos.CaseSummary;
import edu.demo.scfs.web.dto.CaseDtos.CaseDetail;
import edu.demo.scfs.web.dto.CaseDtos.PrivateNoteRequest;
import edu.demo.scfs.web.dto.CaseDtos.PrivateNoteSummary;
import edu.demo.scfs.web.dto.CaseDtos.ReminderRequest;
import edu.demo.scfs.web.dto.CaseDtos.SearchFilters;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/cases")
public class CaseAdvancedController {
    private final CaseService cases;
    private final CaseAttachmentRepository attachments;
    private final FileStorageService fileStorage;

    public CaseAdvancedController(CaseService cases, CaseAttachmentRepository attachments, FileStorageService fileStorage) {
        this.cases = cases;
        this.attachments = attachments;
        this.fileStorage = fileStorage;
    }

    @GetMapping("/search")
    public List<CaseSummary> search(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) CaseStatus status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Boolean anonymous,
            @RequestParam(required = false) CasePriority priority,
            @RequestParam(required = false) Boolean overdue
    ) {
        return cases.searchCases(principal.user(), new SearchFilters(keyword, status, categoryId, departmentId, anonymous, priority, overdue));
    }

    @GetMapping("/overdue")
    public List<CaseSummary> overdue(@AuthenticationPrincipal UserPrincipal principal) {
        return cases.overdueCases(principal.user());
    }

    @GetMapping("/{id}")
    public CaseDetail detail(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return cases.caseDetail(principal.user(), id);
    }

    @PostMapping("/{id}/reminders")
    public void reminder(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id, @RequestBody ReminderRequest request) {
        cases.addReminder(principal.user(), id, request);
    }

    @GetMapping("/{id}/private-notes")
    public List<PrivateNoteSummary> privateNotes(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return cases.privateNotes(principal.user(), id);
    }

    @PostMapping("/{id}/private-notes")
    public PrivateNoteSummary addPrivateNote(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody PrivateNoteRequest request
    ) {
        return cases.addPrivateNote(principal.user(), id, request);
    }

    @GetMapping("/{caseId}/attachments/{attachmentId}/download")
    public ResponseEntity<Resource> download(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long caseId,
            @PathVariable Long attachmentId
    ) {
        CaseAttachment attachment = attachment(caseId, attachmentId);
        if (!cases.canAccessAttachment(principal.user(), attachment)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Attachment is not visible to this account");
        }
        Resource resource = fileStorage.asResource(attachment);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(attachment.getOriginalFileName()).build().toString())
                .body(resource);
    }

    @DeleteMapping("/{caseId}/attachments/{attachmentId}")
    public void delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long caseId,
            @PathVariable Long attachmentId
    ) {
        CaseAttachment attachment = attachment(caseId, attachmentId);
        if (!cases.canAccessAttachment(principal.user(), attachment)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Attachment is not visible to this account");
        }
        fileStorage.delete(attachment);
        attachments.delete(attachment);
    }

    private CaseAttachment attachment(Long caseId, Long attachmentId) {
        CaseAttachment attachment = attachments.findById(attachmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment not found"));
        if (!attachment.getComplaintCase().getId().equals(caseId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment not found");
        }
        return attachment;
    }
}
