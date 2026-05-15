package edu.demo.scfs.repository;

import edu.demo.scfs.domain.CaseStatusLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseStatusLogRepository extends JpaRepository<CaseStatusLog, Long> {
    List<CaseStatusLog> findByComplaintCaseIdOrderByCreatedAtAsc(Long caseId);
}

