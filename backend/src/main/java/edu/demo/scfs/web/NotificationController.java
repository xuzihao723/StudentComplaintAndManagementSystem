package edu.demo.scfs.web;

import edu.demo.scfs.security.UserPrincipal;
import edu.demo.scfs.service.NotificationService;
import edu.demo.scfs.web.dto.CaseDtos.NotificationSummary;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notifications;

    public NotificationController(NotificationService notifications) {
        this.notifications = notifications;
    }

    @GetMapping
    public List<NotificationSummary> list(@AuthenticationPrincipal UserPrincipal principal) {
        return notifications.listFor(principal.user());
    }

    @PostMapping("/{id}/read")
    public NotificationSummary markRead(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return notifications.markRead(principal.user(), id);
    }
}
