package edu.demo.scfs.repository;

import edu.demo.scfs.domain.CaseAttachment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseAttachmentRepository extends JpaRepository<CaseAttachment, Long> {
    List<CaseAttachment> findByComplaintCaseIdOrderByUploadedAtAsc(Long caseId);
}

