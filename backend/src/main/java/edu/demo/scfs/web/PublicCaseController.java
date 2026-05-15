package edu.demo.scfs.web;

import edu.demo.scfs.domain.CasePriority;
import edu.demo.scfs.service.CaseService;
import edu.demo.scfs.web.dto.CaseDtos.CaseDetail;
import edu.demo.scfs.web.dto.CaseDtos.PublicCaseResponse;
import edu.demo.scfs.web.dto.CaseDtos.PublicMessageRequest;
import edu.demo.scfs.web.dto.CaseDtos.TrackCaseRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/public/cases")
public class PublicCaseController {
    private final CaseService cases;

    public PublicCaseController(CaseService cases) {
        this.cases = cases;
    }

    @PostMapping
    public PublicCaseResponse submit(
            @RequestParam Long categoryId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) String publicContactEmail,
            @RequestParam(required = false) String publicSubmitterType,
            @RequestParam(required = false) CasePriority priority,
            @RequestPart(required = false) List<MultipartFile> files
    ) {
        return cases.submitPublicCase(categoryId, title, description, publicContactEmail, publicSubmitterType, priority, files);
    }

    @PostMapping("/track")
    public CaseDetail track(@Valid @RequestBody TrackCaseRequest request) {
        return cases.trackPublicCase(request.caseNumber(), request.trackingCode());
    }

    @PostMapping("/track/messages")
    public CaseDetail message(@Valid @RequestBody PublicMessageRequest request) {
        return cases.addPublicMessage(request.caseNumber(), request.trackingCode(), request.content());
    }
}
