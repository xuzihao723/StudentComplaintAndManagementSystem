package edu.demo.scfs.repository;

import edu.demo.scfs.domain.CaseStatus;
import edu.demo.scfs.domain.ComplaintCase;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintCaseRepository extends JpaRepository<ComplaintCase, Long> {
    boolean existsByCaseNumber(String caseNumber);

    Optional<ComplaintCase> findByCaseNumber(String caseNumber);

    List<ComplaintCase> findBySubmittedByIdOrderByUpdatedAtDesc(Long submittedById);

    List<ComplaintCase> findByStatusInOrderByUpdatedAtDesc(Collection<CaseStatus> statuses);

    List<ComplaintCase> findByAssignedDepartmentIdOrderByUpdatedAtDesc(Long departmentId);

    List<ComplaintCase> findBySubmittedAtBetween(Instant start, Instant end);
}

