package edu.demo.scfs.repository;

import edu.demo.scfs.domain.WeeklyReport;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeeklyReportRepository extends JpaRepository<WeeklyReport, Long> {
    List<WeeklyReport> findAllByOrderByGeneratedAtDesc();
}

