package edu.demo.scfs.service;

import edu.demo.scfs.domain.AppUser;
import edu.demo.scfs.domain.CasePriority;
import edu.demo.scfs.domain.Category;
import edu.demo.scfs.domain.Notification;
import edu.demo.scfs.repository.CategoryRepository;
import edu.demo.scfs.repository.NotificationRepository;
import edu.demo.scfs.repository.UserRepository;
import edu.demo.scfs.web.dto.CaseDtos.CaseDetail;
import edu.demo.scfs.web.dto.CaseDtos.TextRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:case-notification-flow;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CaseNotificationFlowTest {
    @Autowired
    private CaseService cases;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notifications;

    @Autowired
    private UserRepository users;

    @Autowired
    private CategoryRepository categories;

    @Test
    void studentSubmissionNotifiesStudentAndOfficersWithCaseMetadata() {
        AppUser student = user("student1");
        AppUser officer = user("officer");
        Category category = categories.findByName("Campus Facilities").orElseThrow();

        CaseDetail detail = cases.submitCase(student, category.getId(), "Broken heater", "Room is cold", false, CasePriority.NORMAL, List.of());

        List<Notification> studentNotifications = notifications.findByRecipientIdOrderByCreatedAtDesc(student.getId());
        List<Notification> officerNotifications = notifications.findByRecipientIdOrderByCreatedAtDesc(officer.getId());

        assertThat(studentNotifications)
                .anySatisfy(item -> assertBoundToCase(item, detail.id(), detail.caseNumber(), "CASE_SUBMITTED"));
        assertThat(officerNotifications)
                .anySatisfy(item -> assertBoundToCase(item, detail.id(), detail.caseNumber(), "NEW_CASE_SUBMITTED"));
    }

    @Test
    void studentMessageNotifiesOfficersWithLatestContent() {
        AppUser student = user("student1");
        AppUser officer = user("officer");
        Category category = categories.findByName("Campus Facilities").orElseThrow();
        CaseDetail detail = cases.submitCase(student, category.getId(), "Broken heater", "Room is cold", false, CasePriority.NORMAL, List.of());

        cases.addStudentMessage(student, detail.id(), new TextRequest("It is still not fixed."));

        assertThat(notifications.findByRecipientIdOrderByCreatedAtDesc(officer.getId()))
                .anySatisfy(item -> {
                    assertBoundToCase(item, detail.id(), detail.caseNumber(), "STUDENT_MESSAGE_ADDED");
                    assertThat(item.getContent()).contains("It is still not fixed.");
                });
    }

    @Test
    void staffUpdatesNotifyStudentWithCaseMetadata() {
        AppUser student = user("student1");
        AppUser officer = user("officer");
        AppUser staff = user("facility_staff");
        Category category = categories.findByName("Campus Facilities").orElseThrow();
        CaseDetail detail = cases.submitCase(student, category.getId(), "Broken heater", "Room is cold", false, CasePriority.NORMAL, List.of());

        cases.requestInformation(officer, detail.id(), new TextRequest("Please add a room number."));
        cases.assignCase(officer, detail.id(), new edu.demo.scfs.web.dto.CaseDtos.AssignRequest(staff.getDepartment().getId(), List.of(staff.getDepartment().getId()), "Facilities lead"));
        cases.updateProgress(staff, detail.id(), new TextRequest("Technician is checking it today."));
        cases.resolveCase(staff, detail.id(), new TextRequest("The heater has been repaired."));

        assertThat(notifications.findByRecipientIdOrderByCreatedAtDesc(student.getId()))
                .anySatisfy(item -> assertBoundToCase(item, detail.id(), detail.caseNumber(), "INFO_REQUESTED"))
                .anySatisfy(item -> assertBoundToCase(item, detail.id(), detail.caseNumber(), "PROGRESS_UPDATED"))
                .anySatisfy(item -> assertBoundToCase(item, detail.id(), detail.caseNumber(), "CASE_RESOLVED"));
    }

    @Test
    void onlyRecipientCanMarkNotificationRead() {
        AppUser student = user("student1");
        AppUser officer = user("officer");
        Category category = categories.findByName("Campus Facilities").orElseThrow();
        CaseDetail detail = cases.submitCase(student, category.getId(), "Broken heater", "Room is cold", false, CasePriority.NORMAL, List.of());
        Notification officerNotification = notifications.findByRecipientIdOrderByCreatedAtDesc(officer.getId()).stream()
                .filter(item -> detail.caseNumber().equals(item.getCaseNumber()))
                .findFirst()
                .orElseThrow();

        notificationService.markRead(officer, officerNotification.getId());

        assertThat(notifications.findById(officerNotification.getId()).orElseThrow().isReadFlag()).isTrue();
        assertThatThrownBy(() -> notificationService.markRead(student, officerNotification.getId()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Notification not found");
    }

    private AppUser user(String username) {
        return users.findByUsername(username).orElseThrow();
    }

    private void assertBoundToCase(Notification item, Long caseId, String caseNumber, String actionType) {
        assertThat(item.getCaseId()).isEqualTo(caseId);
        assertThat(item.getCaseNumber()).isEqualTo(caseNumber);
        assertThat(item.getActionType()).isEqualTo(actionType);
    }
}
