package edu.demo.scfs.service;

import edu.demo.scfs.domain.Category;
import edu.demo.scfs.domain.Department;
import edu.demo.scfs.domain.WorkflowTemplate;
import edu.demo.scfs.repository.CategoryRepository;
import edu.demo.scfs.repository.DepartmentRepository;
import edu.demo.scfs.web.dto.AdminReferenceDtos.CategoryRequest;
import edu.demo.scfs.web.dto.AdminReferenceDtos.DepartmentRequest;
import edu.demo.scfs.web.dto.ReferenceDtos.CategorySummary;
import edu.demo.scfs.web.dto.ReferenceDtos.DepartmentSummary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdminReferenceService {
    private final DepartmentRepository departments;
    private final CategoryRepository categories;
    private final ReferenceService referenceService;

    public AdminReferenceService(
            DepartmentRepository departments,
            CategoryRepository categories,
            ReferenceService referenceService
    ) {
        this.departments = departments;
        this.categories = categories;
        this.referenceService = referenceService;
    }

    @Transactional
    public DepartmentSummary createDepartment(DepartmentRequest request) {
        Department item = new Department();
        item.setName(request.name());
        item.setDescription(request.description());
        item.setEnabled(request.enabled());
        Department saved = departments.save(item);
        return new DepartmentSummary(saved.getId(), saved.getName(), saved.getDescription(), saved.isEnabled());
    }

    @Transactional
    public DepartmentSummary updateDepartment(Long id, DepartmentRequest request) {
        Department item = departments.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));
        item.setName(request.name());
        item.setDescription(request.description());
        item.setEnabled(request.enabled());
        return new DepartmentSummary(item.getId(), item.getName(), item.getDescription(), item.isEnabled());
    }

    @Transactional
    public CategorySummary createCategory(CategoryRequest request) {
        Category item = new Category();
        applyCategory(item, request);
        Category savedItem = categories.save(item);
        return referenceService.categories().stream()
                .filter(saved -> saved.id().equals(savedItem.getId()))
                .findFirst()
                .orElseThrow();
    }

    @Transactional
    public CategorySummary updateCategory(Long id, CategoryRequest request) {
        Category item = categories.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        applyCategory(item, request);
        categories.save(item);
        return referenceService.categories().stream()
                .filter(saved -> saved.id().equals(id))
                .findFirst()
                .orElseThrow();
    }

    private void applyCategory(Category item, CategoryRequest request) {
        Department department = departments.findById(request.defaultDepartmentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Department not found"));
        item.setName(request.name());
        item.setDescription(request.description());
        item.setAnonymousAllowed(request.anonymousAllowed());
        item.setEnabled(request.enabled());
        item.setDefaultDepartment(department);
        item.setDefaultSlaHours(request.defaultSlaHours() == null ? 72 : Math.max(1, request.defaultSlaHours()));
        item.setWorkflowTemplate(request.workflowTemplate() == null ? WorkflowTemplate.STANDARD : request.workflowTemplate());
    }
}
