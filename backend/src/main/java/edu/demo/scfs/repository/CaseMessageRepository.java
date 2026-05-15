package edu.demo.scfs.repository;

import edu.demo.scfs.domain.CaseMessage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseMessageRepository extends JpaRepository<CaseMessage, Long> {
    List<CaseMessage> findByComplaintCaseIdOrderByCreatedAtAsc(Long caseId);
}

