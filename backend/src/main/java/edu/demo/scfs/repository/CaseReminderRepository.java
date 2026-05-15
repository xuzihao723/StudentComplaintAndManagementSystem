package edu.demo.scfs.repository;

import edu.demo.scfs.domain.CaseReminder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseReminderRepository extends JpaRepository<CaseReminder, Long> {
}
