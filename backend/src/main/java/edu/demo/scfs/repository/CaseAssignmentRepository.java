package edu.demo.scfs.repository;

import edu.demo.scfs.domain.CaseAssignment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseAssignmentRepository extends JpaRepository<CaseAssignment, Long> {
    List<CaseAssignment> findByComplaintCaseIdOrderByAssignedAtAsc(Long caseId);

    List<CaseAssignment> findByDepartmentIdOrderByAssignedAtDesc(Long departmentId);

    boolean existsByComplaintCaseIdAndDepartmentId(Long caseId, Long departmentId);

    void deleteByComplaintCaseId(Long caseId);
}
