package edu.demo.scfs.web;

import edu.demo.scfs.service.AdminReferenceService;
import edu.demo.scfs.web.dto.AdminReferenceDtos.CategoryRequest;
import edu.demo.scfs.web.dto.AdminReferenceDtos.DepartmentRequest;
import edu.demo.scfs.web.dto.ReferenceDtos.CategorySummary;
import edu.demo.scfs.web.dto.ReferenceDtos.DepartmentSummary;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/reference")
public class AdminReferenceController {
    private final AdminReferenceService references;

    public AdminReferenceController(AdminReferenceService references) {
        this.references = references;
    }

    @PostMapping("/departments")
    public DepartmentSummary createDepartment(@Valid @RequestBody DepartmentRequest request) {
        return references.createDepartment(request);
    }

    @PutMapping("/departments/{id}")
    public DepartmentSummary updateDepartment(@PathVariable Long id, @Valid @RequestBody DepartmentRequest request) {
        return references.updateDepartment(id, request);
    }

    @PostMapping("/categories")
    public CategorySummary createCategory(@Valid @RequestBody CategoryRequest request) {
        return references.createCategory(request);
    }

    @PutMapping("/categories/{id}")
    public CategorySummary updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return references.updateCategory(id, request);
    }
}

