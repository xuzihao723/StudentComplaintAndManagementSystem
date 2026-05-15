package edu.demo.scfs.repository;

import edu.demo.scfs.domain.CasePrivateNote;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CasePrivateNoteRepository extends JpaRepository<CasePrivateNote, Long> {
    List<CasePrivateNote> findByComplaintCaseIdOrderByCreatedAtDesc(Long caseId);
}
