package edu.demo.scfs.service;

import edu.demo.scfs.domain.AppUser;
import edu.demo.scfs.domain.ComplaintCase;
import edu.demo.scfs.domain.EmailStatus;
import edu.demo.scfs.domain.Notification;
import edu.demo.scfs.repository.NotificationRepository;
import edu.demo.scfs.web.dto.CaseDtos.NotificationSummary;
import java.util.List;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class NotificationService {
    private final NotificationRepository notifications;
    private final SettingsService settings;

    public NotificationService(NotificationRepository notifications, SettingsService settings) {
        this.notifications = notifications;
        this.settings = settings;
    }

    @Transactional
    public Notification notifyUser(AppUser recipient, String title, String content) {
        return notifyCaseUser(recipient, null, null, title, content);
    }

    @Transactional
    public Notification notifyCaseUser(AppUser recipient, ComplaintCase complaintCase, String actionType, String title, String content) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        if (complaintCase != null) {
            notification.setCaseId(complaintCase.getId());
            notification.setCaseNumber(complaintCase.getCaseNumber());
        }
        notification.setActionType(actionType);
        notification.setTitle(title);
        notification.setContent(content);
        notifications.save(notification);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipient.getEmail());
            message.setSubject(title);
            message.setText(content);
            settings.sendMail(message);
            notification.setEmailStatus(EmailStatus.SENT);
        } catch (RuntimeException ex) {
            notification.setEmailStatus(EmailStatus.FAILED);
            notification.setFailureReason(ex.getMessage() == null ? "Email send failed" : ex.getMessage());
        }
        return notification;
    }

    @Transactional
    public NotificationSummary markRead(AppUser user, Long notificationId) {
        Notification notification = notifications.findByIdAndRecipientId(notificationId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        notification.setReadFlag(true);
        return summary(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationSummary> listFor(AppUser user) {
        return notifications.findByRecipientIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::summary)
                .toList();
    }

    private NotificationSummary summary(Notification item) {
        return new NotificationSummary(
                item.getId(),
                item.getTitle(),
                item.getContent(),
                item.getCaseId(),
                item.getCaseNumber(),
                item.getActionType(),
                item.getEmailStatus(),
                item.getFailureReason(),
                item.isReadFlag(),
                item.getCreatedAt()
        );
    }
}
