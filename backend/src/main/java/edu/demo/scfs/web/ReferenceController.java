package edu.demo.scfs.web;

import edu.demo.scfs.service.ReferenceService;
import edu.demo.scfs.web.dto.ReferenceDtos.CategorySummary;
import edu.demo.scfs.web.dto.ReferenceDtos.DepartmentSummary;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reference")
public class ReferenceController {
    private final ReferenceService references;

    public ReferenceController(ReferenceService references) {
        this.references = references;
    }

    @GetMapping("/departments")
    public List<DepartmentSummary> departments() {
        return references.departments();
    }

    @GetMapping("/categories")
    public List<CategorySummary> categories() {
        return references.categories();
    }
}

