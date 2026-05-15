package edu.demo.scfs.service;

import edu.demo.scfs.domain.AppUser;
import edu.demo.scfs.domain.AssignmentRole;
import edu.demo.scfs.domain.AuditLog;
import edu.demo.scfs.domain.CaseAssignment;
import edu.demo.scfs.domain.CaseAttachment;
import edu.demo.scfs.domain.CaseMessage;
import edu.demo.scfs.domain.CasePriority;
import edu.demo.scfs.domain.CasePrivateNote;
import edu.demo.scfs.domain.CaseReminder;
import edu.demo.scfs.domain.CaseStatus;
import edu.demo.scfs.domain.CaseStatusLog;
import edu.demo.scfs.domain.Category;
import edu.demo.scfs.domain.ComplaintCase;
import edu.demo.scfs.domain.Department;
import edu.demo.scfs.domain.MessageType;
import edu.demo.scfs.domain.Role;
import edu.demo.scfs.domain.WorkflowTemplate;
import edu.demo.scfs.repository.AuditLogRepository;
import edu.demo.scfs.repository.CaseAssignmentRepository;
import edu.demo.scfs.repository.CaseAttachmentRepository;
import edu.demo.scfs.repository.CaseMessageRepository;
import edu.demo.scfs.repository.CasePrivateNoteRepository;
import edu.demo.scfs.repository.CaseReminderRepository;
import edu.demo.scfs.repository.CaseStatusLogRepository;
import edu.demo.scfs.repository.CategoryRepository;
import edu.demo.scfs.repository.ComplaintCaseRepository;
import edu.demo.scfs.repository.DepartmentRepository;
import edu.demo.scfs.repository.UserRepository;
import edu.demo.scfs.util.CaseNumberGenerator;
import edu.demo.scfs.web.dto.CaseDtos.AssignRequest;
import edu.demo.scfs.web.dto.CaseDtos.AuditLogSummary;
import edu.demo.scfs.web.dto.CaseDtos.CaseDetail;
import edu.demo.scfs.web.dto.CaseDtos.CaseSummary;
import edu.demo.scfs.web.dto.CaseDtos.DashboardSummary;
import edu.demo.scfs.web.dto.CaseDtos.PrivateNoteRequest;
import edu.demo.scfs.web.dto.CaseDtos.PrivateNoteSummary;
import edu.demo.scfs.web.dto.CaseDtos.PublicCaseResponse;
import edu.demo.scfs.web.dto.CaseDtos.ReminderRequest;
import edu.demo.scfs.web.dto.CaseDtos.SatisfactionRequest;
import edu.demo.scfs.web.dto.CaseDtos.SearchFilters;
import edu.demo.scfs.web.dto.CaseDtos.TextRequest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CaseService {
    private final ComplaintCaseRepository cases;
    private final CategoryRepository categories;
    private final DepartmentRepository departments;
    private final UserRepository users;
    private final CaseAttachmentRepository attachments;
    private final CaseMessageRepository messages;
    private final CaseStatusLogRepository logs;
    private final CaseAssignmentRepository assignments;
    private final CasePrivateNoteRepository privateNotes;
    private final AuditLogRepository auditLogs;
    private final CaseReminderRepository reminders;
    private final FileStorageService fileStorage;
    private final NotificationService notifications;
    private final CaseMapper mapper;
    private final TrackingCodeService trackingCodes;
    private final CaseNumberGenerator caseNumberGenerator = new CaseNumberGenerator();

    public CaseService(
            ComplaintCaseRepository cases,
            CategoryRepository categories,
            DepartmentRepository departments,
            UserRepository users,
            CaseAttachmentRepository attachments,
            CaseMessageRepository messages,
            CaseStatusLogRepository logs,
            CaseAssignmentRepository assignments,
            CasePrivateNoteRepository privateNotes,
            AuditLogRepository auditLogs,
            CaseReminderRepository reminders,
            FileStorageService fileStorage,
            NotificationService notifications,
            CaseMapper mapper,
            TrackingCodeService trackingCodes
    ) {
        this.cases = cases;
        this.categories = categories;
        this.departments = departments;
        this.users = users;
        this.attachments = attachments;
        this.messages = messages;
        this.logs = logs;
        this.assignments = assignments;
        this.privateNotes = privateNotes;
        this.auditLogs = auditLogs;
        this.reminders = reminders;
        this.fileStorage = fileStorage;
        this.notifications = notifications;
        this.mapper = mapper;
        this.trackingCodes = trackingCodes;
    }

    @Transactional
    public CaseDetail submitCase(
            AppUser student,
            Long categoryId,
            String title,
            String description,
            boolean anonymous,
            CasePriority priority,
            List<MultipartFile> files
    ) {
        Category category = category(categoryId);
        CategoryPolicy.validateAnonymous(category, anonymous);

        ComplaintCase item = baseCase(category, title, description, priority);
        item.setSubmittedBy(student);
        item.setAnonymous(anonymous);
        ComplaintCase saved = cases.save(item);
        List<CaseAttachment> stored = attachments.saveAll(fileStorage.storeFiles(saved, files));
        addLog(saved, null, saved.getStatus(), student, "Case submitted");
        audit(student, "CASE_SUBMITTED", "CASE", saved.getCaseNumber(), "Student case submitted");
        notifications.notifyCaseUser(student, saved, "CASE_SUBMITTED", "Case submitted: " + saved.getCaseNumber(), "Your case has been submitted successfully.");
        notifyRole(Role.OFFICER, saved, "NEW_CASE_SUBMITTED", "New student case: " + saved.getCaseNumber(), title);
        return mapper.toDetail(saved, stored, List.of(), logs.findByComplaintCaseIdOrderByCreatedAtAsc(saved.getId()), List.of(), true);
    }

    @Transactional
    public PublicCaseResponse submitPublicCase(
            Long categoryId,
            String title,
            String description,
            String publicContactEmail,
            String publicSubmitterType,
            CasePriority priority,
            List<MultipartFile> files
    ) {
        Category category = category(categoryId);
        if (!category.isAnonymousAllowed()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This category does not allow public anonymous submission");
        }

        String trackingCode = trackingCodes.generatePlainCode();
        ComplaintCase item = baseCase(category, title, description, priority);
        item.setAnonymous(true);
        item.setPublicContactEmail(blankToNull(publicContactEmail));
        item.setPublicSubmitterType(blankToNull(publicSubmitterType) == null ? "Anonymous public submitter" : publicSubmitterType.trim());
        item.setTrackingCodeHash(trackingCodes.hash(trackingCode));
        ComplaintCase saved = cases.save(item);
        List<CaseAttachment> stored = attachments.saveAll(fileStorage.storeFiles(saved, files));
        addMessage(saved, null, "Public anonymous case submitted.", MessageType.ADDITIONAL_INFO, "Anonymous submitter", "PUBLIC");
        addLog(saved, null, saved.getStatus(), null, "Public anonymous case submitted");
        audit(null, "PUBLIC_CASE_SUBMITTED", "CASE", saved.getCaseNumber(), "Public anonymous case submitted");
        notifyRole(Role.OFFICER, saved, "PUBLIC_CASE_SUBMITTED", "Public anonymous case: " + saved.getCaseNumber(), "A public anonymous case is waiting for review.");
        CaseDetail detail = mapper.toDetail(saved, stored, messages.findByComplaintCaseIdOrderByCreatedAtAsc(saved.getId()), logs.findByComplaintCaseIdOrderByCreatedAtAsc(saved.getId()), List.of(), false);
        return new PublicCaseResponse(detail, trackingCode);
    }

    @Transactional(readOnly = true)
    public CaseDetail trackPublicCase(String caseNumber, String trackingCode) {
        ComplaintCase item = publicTrackedCase(caseNumber, trackingCode);
        return detail(item, false);
    }

    @Transactional
    public CaseDetail addPublicMessage(String caseNumber, String trackingCode, String content) {
        ComplaintCase item = publicTrackedCase(caseNumber, trackingCode);
        addMessage(item, null, content, MessageType.ADDITIONAL_INFO, "Anonymous submitter", "PUBLIC");
        touch(item);
        audit(null, "PUBLIC_MESSAGE_ADDED", "CASE", item.getCaseNumber(), "Anonymous submitter added a message");
        notifyRole(Role.OFFICER, item, "PUBLIC_MESSAGE_ADDED", "Anonymous message: " + item.getCaseNumber(), content);
        return detail(item, false);
    }

    @Transactional(readOnly = true)
    public List<CaseSummary> studentCases(AppUser student) {
        return cases.findBySubmittedByIdOrderByUpdatedAtDesc(student.getId()).stream()
                .map(item -> mapper.toSummary(item, true))
                .toList();
    }

    @Transactional
    public CaseDetail studentCase(AppUser student, Long caseId) {
        ComplaintCase item = caseForStudent(student, caseId);
        audit(student, "CASE_VIEWED", "CASE", item.getCaseNumber(), "Student viewed case detail");
        return detail(item, true);
    }

    @Transactional
    public CaseDetail addStudentMessage(AppUser student, Long caseId, TextRequest request) {
        ComplaintCase item = caseForStudent(student, caseId);
        addMessage(item, student, request.content(), MessageType.ADDITIONAL_INFO);
        if (item.getStatus() == CaseStatus.AWAITING_STUDENT_INFO) {
            changeStatus(item, CaseStatus.UNDER_REVIEW, student, "Student provided additional information");
        } else {
            touch(item);
        }
        audit(student, "CASE_MESSAGE_ADDED", "CASE", item.getCaseNumber(), "Student added a message");
        notifyRole(Role.OFFICER, item, "STUDENT_MESSAGE_ADDED", "Student added information: " + item.getCaseNumber(), request.content());
        return detail(item, true);
    }

    @Transactional
    public CaseDetail requestFollowUp(AppUser student, Long caseId, TextRequest request) {
        ComplaintCase item = caseForStudent(student, caseId);
        if (item.getStatus() != CaseStatus.RESOLVED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only resolved cases can request follow-up review");
        }
        addMessage(item, student, request.content(), MessageType.FOLLOW_UP);
        changeStatus(item, CaseStatus.FOLLOW_UP_REQUESTED, student, "Student requested follow-up review");
        audit(student, "FOLLOW_UP_REQUESTED", "CASE", item.getCaseNumber(), "Student requested follow-up review");
        notifyRole(Role.OFFICER, item, "FOLLOW_UP_REQUESTED", "Follow-up requested: " + item.getCaseNumber(), "A student requested follow-up review.");
        return detail(item, true);
    }

    @Transactional
    public CaseDetail requestReopen(AppUser student, Long caseId, TextRequest request) {
        ComplaintCase item = caseForStudent(student, caseId);
        if (item.getStatus() != CaseStatus.CLOSED && item.getStatus() != CaseStatus.RESOLVED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only resolved or closed cases can be reopened");
        }
        item.setReopenedAt(Instant.now());
        item.setClosedAt(null);
        addMessage(item, student, request.content(), MessageType.FOLLOW_UP);
        changeStatus(item, CaseStatus.REOPEN_REQUESTED, student, "Student requested case reopen");
        audit(student, "REOPEN_REQUESTED", "CASE", item.getCaseNumber(), "Student requested reopen");
        notifyRole(Role.OFFICER, item, "REOPEN_REQUESTED", "Reopen requested: " + item.getCaseNumber(), request.content());
        return detail(item, true);
    }

    @Transactional
    public CaseDetail submitSatisfaction(AppUser student, Long caseId, SatisfactionRequest request) {
        ComplaintCase item = caseForStudent(student, caseId);
        if (item.getStatus() != CaseStatus.RESOLVED && item.getStatus() != CaseStatus.CLOSED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Satisfaction can be submitted after resolution");
        }
        item.setSatisfactionRating(request.rating());
        item.setSatisfactionComment(blankToNull(request.comment()));
        item.setSatisfactionAt(Instant.now());
        touch(item);
        audit(student, "SATISFACTION_SUBMITTED", "CASE", item.getCaseNumber(), "Rating: " + request.rating());
        return detail(item, true);
    }

    @Transactional(readOnly = true)
    public List<CaseSummary> officerQueue() {
        return cases.findByStatusInOrderByUpdatedAtDesc(List.of(
                        CaseStatus.SUBMITTED,
                        CaseStatus.UNDER_REVIEW,
                        CaseStatus.AWAITING_STUDENT_INFO,
                        CaseStatus.FOLLOW_UP_REQUESTED,
                        CaseStatus.REOPEN_REQUESTED,
                        CaseStatus.URGENT_REVIEW
                )).stream()
                .map(item -> mapper.toSummary(item, false))
                .toList();
    }

    @Transactional(readOnly = true)
    public CaseDetail staffCase(Long caseId) {
        ComplaintCase item = cases.findById(caseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Case not found"));
        return detail(item, false);
    }

    @Transactional
    public CaseDetail caseDetail(AppUser actor, Long caseId) {
        ComplaintCase item = cases.findById(caseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Case not found"));
        if (!canAccess(actor, item)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Case is not visible to this account");
        }
        audit(actor, "CASE_VIEWED", "CASE", item.getCaseNumber(), "Case detail viewed");
        return detail(item, actor.getRole() == Role.STUDENT);
    }

    @Transactional
    public CaseDetail assignCase(AppUser officer, Long caseId, AssignRequest request) {
        ComplaintCase item = cases.findById(caseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Case not found"));
        List<Long> departmentIds = assignmentDepartmentIds(request);
        assignments.deleteByComplaintCaseId(item.getId());

        Department lead = null;
        for (int index = 0; index < departmentIds.size(); index++) {
            Department department = departments.findById(departmentIds.get(index))
                    .filter(Department::isEnabled)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Department not found"));
            if (index == 0) {
                lead = department;
            }
            CaseAssignment assignment = new CaseAssignment();
            assignment.setComplaintCase(item);
            assignment.setDepartment(department);
            assignment.setAssignmentRole(index == 0 ? AssignmentRole.LEAD : AssignmentRole.COLLABORATOR);
            assignments.save(assignment);
            notifyDepartment(item, department, "CASE_ASSIGNED", "Case assigned: " + item.getCaseNumber(), "A case has been assigned to your department.");
        }
        item.setAssignedDepartment(lead);
        changeStatus(item, CaseStatus.ASSIGNED, officer, request.note() == null ? "Assigned to department" : request.note());
        audit(officer, "CASE_ASSIGNED", "CASE", item.getCaseNumber(), "Departments: " + departmentIds);
        return detail(item, false);
    }

    @Transactional
    public CaseDetail requestInformation(AppUser officer, Long caseId, TextRequest request) {
        ComplaintCase item = cases.findById(caseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Case not found"));
        addMessage(item, officer, request.content(), MessageType.PROGRESS_NOTE);
        changeStatus(item, CaseStatus.AWAITING_STUDENT_INFO, officer, "Requested additional information");
        notifySubmitter(item, "INFO_REQUESTED", "More information needed: " + item.getCaseNumber(), request.content());
        audit(officer, "INFO_REQUESTED", "CASE", item.getCaseNumber(), "Officer requested additional information");
        return detail(item, false);
    }

    @Transactional
    public CaseDetail closeCase(AppUser officer, Long caseId, TextRequest request) {
        ComplaintCase item = cases.findById(caseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Case not found"));
        item.setClosedAt(Instant.now());
        changeStatus(item, CaseStatus.CLOSED, officer, request.content());
        notifySubmitter(item, "CASE_CLOSED", "Case closed: " + item.getCaseNumber(), request.content());
        audit(officer, "CASE_CLOSED", "CASE", item.getCaseNumber(), "Officer closed case");
        return detail(item, false);
    }

    @Transactional(readOnly = true)
    public List<CaseSummary> departmentCases(AppUser staff) {
        if (staff.getDepartment() == null) {
            return List.of();
        }
        Set<ComplaintCase> visible = new LinkedHashSet<>(cases.findByAssignedDepartmentIdOrderByUpdatedAtDesc(staff.getDepartment().getId()));
        assignments.findByDepartmentIdOrderByAssignedAtDesc(staff.getDepartment().getId()).stream()
                .map(CaseAssignment::getComplaintCase)
                .forEach(visible::add);
        return visible.stream()
                .sorted((left, right) -> right.getUpdatedAt().compareTo(left.getUpdatedAt()))
                .map(item -> mapper.toSummary(item, false))
                .toList();
    }

    @Transactional
    public CaseDetail departmentCase(AppUser staff, Long caseId) {
        ComplaintCase item = departmentScopedCase(staff, caseId);
        audit(staff, "CASE_VIEWED", "CASE", item.getCaseNumber(), "Department staff viewed case detail");
        return detail(item, false);
    }

    @Transactional
    public CaseDetail updateProgress(AppUser staff, Long caseId, TextRequest request) {
        ComplaintCase item = departmentScopedCase(staff, caseId);
        item.setAssignedStaff(staff);
        addMessage(item, staff, request.content(), MessageType.PROGRESS_NOTE);
        changeStatus(item, CaseStatus.IN_PROGRESS, staff, "Progress updated");
        notifySubmitter(item, "PROGRESS_UPDATED", "Case progress updated: " + item.getCaseNumber(), request.content());
        audit(staff, "PROGRESS_UPDATED", "CASE", item.getCaseNumber(), "Department updated progress");
        return detail(item, false);
    }

    @Transactional
    public CaseDetail resolveCase(AppUser staff, Long caseId, TextRequest request) {
        ComplaintCase item = departmentScopedCase(staff, caseId);
        item.setAssignedStaff(staff);
        item.setResolvedAt(Instant.now());
        addMessage(item, staff, request.content(), MessageType.STAFF_RESPONSE);
        changeStatus(item, CaseStatus.RESOLVED, staff, "Case resolved");
        notifySubmitter(item, "CASE_RESOLVED", "Case resolved: " + item.getCaseNumber(), request.content());
        audit(staff, "CASE_RESOLVED", "CASE", item.getCaseNumber(), "Department resolved case");
        return detail(item, false);
    }

    @Transactional(readOnly = true)
    public List<CaseSummary> searchCases(AppUser actor, SearchFilters filters) {
        return visibleCases(actor).stream()
                .filter(item -> matchesFilters(item, filters))
                .sorted((left, right) -> right.getUpdatedAt().compareTo(left.getUpdatedAt()))
                .map(item -> mapper.toSummary(item, actor.getRole() == Role.STUDENT))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CaseSummary> overdueCases(AppUser actor) {
        return searchCases(actor, new SearchFilters(null, null, null, null, null, null, true));
    }

    @Transactional(readOnly = true)
    public DashboardSummary dashboardSummary(AppUser actor) {
        List<ComplaintCase> visible = visibleCases(actor);
        List<CaseSummary> recent = visible.stream()
                .sorted((left, right) -> right.getUpdatedAt().compareTo(left.getUpdatedAt()))
                .limit(5)
                .map(item -> mapper.toSummary(item, actor.getRole() == Role.STUDENT))
                .toList();
        List<CaseSummary> overdue = visible.stream()
                .filter(ComplaintCase::isOverdue)
                .sorted((left, right) -> right.getUpdatedAt().compareTo(left.getUpdatedAt()))
                .map(item -> mapper.toSummary(item, actor.getRole() == Role.STUDENT))
                .toList();
        return new DashboardSummary(
                visible.size(),
                visible.stream().filter(item -> item.getStatus() != CaseStatus.RESOLVED && item.getStatus() != CaseStatus.CLOSED).count(),
                overdue.size(),
                visible.stream().filter(item -> item.getPriority() == CasePriority.HIGH || item.getPriority() == CasePriority.URGENT).count(),
                visible.stream().filter(item -> item.getStatus() == CaseStatus.RESOLVED).count(),
                visible.stream().filter(item -> item.getStatus() == CaseStatus.FOLLOW_UP_REQUESTED || item.getStatus() == CaseStatus.REOPEN_REQUESTED).count(),
                recent,
                overdue
        );
    }

    @Transactional
    public void addReminder(AppUser actor, Long caseId, ReminderRequest request) {
        ComplaintCase item = cases.findById(caseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Case not found"));
        if (!canAccess(actor, item)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Case is not visible to this account");
        }
        CaseReminder reminder = new CaseReminder();
        reminder.setComplaintCase(item);
        reminder.setRequestedBy(actor);
        reminder.setNote(blankToNull(request.note()) == null ? "Reminder requested" : request.note().trim());
        reminders.save(reminder);
        notifyRole(Role.OFFICER, item, "REMINDER_CREATED", "Reminder: " + item.getCaseNumber(), reminder.getNote());
        notifyRole(Role.ADMIN, item, "REMINDER_CREATED", "Reminder: " + item.getCaseNumber(), reminder.getNote());
        audit(actor, "REMINDER_CREATED", "CASE", item.getCaseNumber(), reminder.getNote());
    }

    @Transactional(readOnly = true)
    public List<PrivateNoteSummary> privateNotes(AppUser actor, Long caseId) {
        ComplaintCase item = staffVisibleCase(actor, caseId);
        return privateNotes.findByComplaintCaseIdOrderByCreatedAtDesc(item.getId()).stream()
                .map(note -> new PrivateNoteSummary(note.getId(), note.getAuthor().getFullName(), note.getContent(), note.getRootCause(), note.getCreatedAt()))
                .toList();
    }

    @Transactional
    public PrivateNoteSummary addPrivateNote(AppUser actor, Long caseId, PrivateNoteRequest request) {
        ComplaintCase item = staffVisibleCase(actor, caseId);
        CasePrivateNote note = new CasePrivateNote();
        note.setComplaintCase(item);
        note.setAuthor(actor);
        note.setContent(required(request.content(), "Note"));
        note.setRootCause(blankToNull(request.rootCause()));
        privateNotes.save(note);
        audit(actor, "PRIVATE_NOTE_ADDED", "CASE", item.getCaseNumber(), "Internal note added");
        return new PrivateNoteSummary(note.getId(), actor.getFullName(), note.getContent(), note.getRootCause(), note.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public List<AuditLogSummary> auditLogSummaries() {
        return auditLogs.findTop200ByOrderByCreatedAtDesc().stream()
                .map(item -> new AuditLogSummary(item.getId(), item.getActorName(), item.getAction(), item.getTargetType(), item.getTargetId(), item.getDetail(), item.getCreatedAt()))
                .toList();
    }

    @Transactional
    public int markOverdueCases() {
        Instant now = Instant.now();
        int changed = 0;
        for (ComplaintCase item : cases.findAll()) {
            if (!item.isOverdue()
                    && item.getDueAt() != null
                    && item.getDueAt().isBefore(now)
                    && item.getStatus() != CaseStatus.RESOLVED
                    && item.getStatus() != CaseStatus.CLOSED) {
                item.setOverdue(true);
                item.setEscalatedAt(now);
                addLog(item, item.getStatus(), item.getStatus(), null, "SLA overdue and escalated automatically");
                audit(null, "SLA_OVERDUE", "CASE", item.getCaseNumber(), "Case exceeded due time");
                notifyRole(Role.OFFICER, item, "SLA_OVERDUE", "SLA overdue: " + item.getCaseNumber(), "A case exceeded its due time.");
                notifyRole(Role.ADMIN, item, "SLA_OVERDUE", "SLA overdue: " + item.getCaseNumber(), "A case exceeded its due time.");
                changed++;
            }
        }
        return changed;
    }

    public boolean canAccessAttachment(AppUser actor, CaseAttachment attachment) {
        return canAccess(actor, attachment.getComplaintCase());
    }

    private ComplaintCase baseCase(Category category, String title, String description, CasePriority priority) {
        ComplaintCase item = new ComplaintCase();
        item.setCaseNumber(uniqueCaseNumber());
        item.setCategory(category);
        item.setTitle(required(title, "Title"));
        item.setDescription(required(description, "Description"));
        item.setPriority(priority == null ? CasePriority.NORMAL : priority);
        item.setWorkflowTemplate(category.getWorkflowTemplate() == null ? WorkflowTemplate.STANDARD : category.getWorkflowTemplate());
        item.setStatus(item.getWorkflowTemplate() == WorkflowTemplate.CRISIS ? CaseStatus.URGENT_REVIEW : CaseStatus.SUBMITTED);
        item.setDueAt(SlaPolicy.calculateDueAt(item.getSubmittedAt(), category.getDefaultSlaHours(), item.getPriority()));
        return item;
    }

    private Category category(Long categoryId) {
        return categories.findById(categoryId)
                .filter(Category::isEnabled)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category not found"));
    }

    private ComplaintCase publicTrackedCase(String caseNumber, String trackingCode) {
        ComplaintCase item = cases.findByCaseNumber(caseNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Case not found"));
        if (item.getTrackingCodeHash() == null || !trackingCodes.matches(trackingCode, item.getTrackingCodeHash())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid tracking code");
        }
        return item;
    }

    private ComplaintCase caseForStudent(AppUser student, Long caseId) {
        ComplaintCase item = cases.findById(caseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Case not found"));
        if (item.getSubmittedBy() == null || !item.getSubmittedBy().getId().equals(student.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own cases");
        }
        return item;
    }

    private ComplaintCase departmentScopedCase(AppUser staff, Long caseId) {
        ComplaintCase item = cases.findById(caseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Case not found"));
        if (staff.getDepartment() == null
                || (item.getAssignedDepartment() == null || !item.getAssignedDepartment().getId().equals(staff.getDepartment().getId()))
                && !assignments.existsByComplaintCaseIdAndDepartmentId(item.getId(), staff.getDepartment().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Case is not assigned to your department");
        }
        return item;
    }

    private ComplaintCase staffVisibleCase(AppUser actor, Long caseId) {
        if (actor.getRole() == Role.STUDENT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Students cannot access internal notes");
        }
        if (actor.getRole() == Role.DEPARTMENT_STAFF) {
            return departmentScopedCase(actor, caseId);
        }
        return cases.findById(caseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Case not found"));
    }

    private CaseDetail detail(ComplaintCase item, boolean revealStudent) {
        return mapper.toDetail(
                item,
                attachments.findByComplaintCaseIdOrderByUploadedAtAsc(item.getId()),
                messages.findByComplaintCaseIdOrderByCreatedAtAsc(item.getId()),
                logs.findByComplaintCaseIdOrderByCreatedAtAsc(item.getId()),
                assignments.findByComplaintCaseIdOrderByAssignedAtAsc(item.getId()),
                revealStudent
        );
    }

    private void addMessage(ComplaintCase item, AppUser sender, String content, MessageType type) {
        addMessage(item, sender, content, type, null, null);
    }

    private void addMessage(ComplaintCase item, AppUser sender, String content, MessageType type, String senderDisplayName, String senderRole) {
        CaseMessage message = new CaseMessage();
        message.setComplaintCase(item);
        message.setSender(sender);
        message.setSenderDisplayName(sender == null ? senderDisplayName : sender.getFullName());
        message.setSenderRole(sender == null ? senderRole : sender.getRole().name());
        message.setContent(required(content, "Message"));
        message.setType(type);
        messages.save(message);
    }

    private void changeStatus(ComplaintCase item, CaseStatus newStatus, AppUser operator, String note) {
        CaseStatus oldStatus = item.getStatus();
        item.setStatus(newStatus);
        touch(item);
        addLog(item, oldStatus, newStatus, operator, note);
    }

    private void addLog(ComplaintCase item, CaseStatus oldStatus, CaseStatus newStatus, AppUser operator, String note) {
        CaseStatusLog log = new CaseStatusLog();
        log.setComplaintCase(item);
        log.setOldStatus(oldStatus);
        log.setNewStatus(newStatus);
        log.setOperator(operator);
        log.setNote(note);
        logs.save(log);
    }

    private void audit(AppUser actor, String action, String targetType, String targetId, String detail) {
        AuditLog log = new AuditLog();
        log.setActor(actor);
        log.setActorName(actor == null ? "System/Public" : actor.getFullName());
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setDetail(detail);
        auditLogs.save(log);
    }

    private boolean canAccess(AppUser actor, ComplaintCase item) {
        return switch (actor.getRole()) {
            case ADMIN, OFFICER -> true;
            case STUDENT -> item.getSubmittedBy() != null && item.getSubmittedBy().getId().equals(actor.getId());
            case DEPARTMENT_STAFF -> actor.getDepartment() != null
                    && ((item.getAssignedDepartment() != null && item.getAssignedDepartment().getId().equals(actor.getDepartment().getId()))
                    || assignments.existsByComplaintCaseIdAndDepartmentId(item.getId(), actor.getDepartment().getId()));
        };
    }

    private List<ComplaintCase> visibleCases(AppUser actor) {
        return cases.findAll().stream().filter(item -> canAccess(actor, item)).toList();
    }

    private boolean matchesFilters(ComplaintCase item, SearchFilters filters) {
        if (filters == null) {
            return true;
        }
        String keyword = blankToNull(filters.keyword());
        if (keyword != null) {
            String haystack = (item.getCaseNumber() + " " + item.getTitle() + " " + item.getDescription()).toLowerCase(Locale.ROOT);
            if (!haystack.contains(keyword.toLowerCase(Locale.ROOT))) {
                return false;
            }
        }
        if (filters.status() != null && item.getStatus() != filters.status()) return false;
        if (filters.categoryId() != null && !item.getCategory().getId().equals(filters.categoryId())) return false;
        if (filters.departmentId() != null && (item.getAssignedDepartment() == null || !item.getAssignedDepartment().getId().equals(filters.departmentId()))) return false;
        if (filters.anonymous() != null && item.isAnonymous() != filters.anonymous()) return false;
        if (filters.priority() != null && item.getPriority() != filters.priority()) return false;
        return filters.overdue() == null || item.isOverdue() == filters.overdue();
    }

    private List<Long> assignmentDepartmentIds(AssignRequest request) {
        List<Long> ids = new ArrayList<>();
        if (request.departmentId() != null) {
            ids.add(request.departmentId());
        }
        if (request.departmentIds() != null) {
            request.departmentIds().stream().filter(id -> !ids.contains(id)).forEach(ids::add);
        }
        if (ids.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one department is required");
        }
        return ids;
    }

    private void touch(ComplaintCase item) {
        item.setUpdatedAt(Instant.now());
    }

    private String uniqueCaseNumber() {
        String number;
        do {
            number = caseNumberGenerator.generate();
        } while (cases.existsByCaseNumber(number));
        return number;
    }

    private String required(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, label + " is required");
        }
        return value.trim();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private void notifySubmitter(ComplaintCase item, String actionType, String title, String content) {
        if (item.getSubmittedBy() != null) {
            notifications.notifyCaseUser(item.getSubmittedBy(), item, actionType, title, content);
        }
    }

    private void notifyDepartment(ComplaintCase item, Department department, String actionType, String title, String content) {
        users.findByRole(Role.DEPARTMENT_STAFF).stream()
                .filter(user -> user.getDepartment() != null && user.getDepartment().getId().equals(department.getId()))
                .forEach(user -> notifications.notifyCaseUser(user, item, actionType, title, content));
    }

    private void notifyRole(Role role, String title, String content) {
        users.findByRole(role).forEach(user -> notifications.notifyUser(user, title, content));
    }

    private void notifyRole(Role role, ComplaintCase item, String actionType, String title, String content) {
        users.findByRole(role).forEach(user -> notifications.notifyCaseUser(user, item, actionType, title, content));
    }
}
