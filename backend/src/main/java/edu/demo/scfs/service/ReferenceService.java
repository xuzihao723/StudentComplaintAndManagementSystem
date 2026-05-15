package edu.demo.scfs.service;

import edu.demo.scfs.repository.CategoryRepository;
import edu.demo.scfs.repository.DepartmentRepository;
import edu.demo.scfs.web.dto.ReferenceDtos.CategorySummary;
import edu.demo.scfs.web.dto.ReferenceDtos.DepartmentSummary;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReferenceService {
    private final DepartmentRepository departments;
    private final CategoryRepository categories;

    public ReferenceService(DepartmentRepository departments, CategoryRepository categories) {
        this.departments = departments;
        this.categories = categories;
    }

    @Transactional(readOnly = true)
    public List<DepartmentSummary> departments() {
        return departments.findAll().stream()
                .map(item -> new DepartmentSummary(item.getId(), item.getName(), item.getDescription(), item.isEnabled()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CategorySummary> categories() {
        return categories.findAll().stream()
                .map(item -> new CategorySummary(
                        item.getId(),
                        item.getName(),
                        item.getDescription(),
                        item.isAnonymousAllowed(),
                        item.isEnabled(),
                        item.getDefaultDepartment() == null ? null : item.getDefaultDepartment().getId(),
                        item.getDefaultDepartment() == null ? null : item.getDefaultDepartment().getName(),
                        item.getDefaultSlaHours(),
                        item.getWorkflowTemplate()
                ))
                .toList();
    }
}
