package edu.demo.scfs.service;

import edu.demo.scfs.domain.CaseAttachment;
import edu.demo.scfs.domain.ComplaintCase;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FileStorageService {
    private static final long MAX_SIZE = 10 * 1024 * 1024;
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "image/gif",
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private final Path uploadDir;

    public FileStorageService(@Value("${app.upload-dir}") String uploadDir) {
        this.uploadDir = Path.of(uploadDir);
    }

    public List<CaseAttachment> storeFiles(ComplaintCase complaintCase, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to create upload directory", ex);
        }
        return files.stream()
                .filter(file -> file != null && !file.isEmpty())
                .map(file -> storeFile(complaintCase, file))
                .toList();
    }

    private CaseAttachment storeFile(ComplaintCase complaintCase, MultipartFile file) {
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("File is larger than 10MB.");
        }
        String contentType = file.getContentType() == null ? "application/octet-stream" : file.getContentType();
        if (!ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Unsupported file type: " + contentType);
        }
        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "attachment" : file.getOriginalFilename());
        String extension = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) {
            extension = original.substring(dot);
        }
        Path target = uploadDir.resolve(UUID.randomUUID() + extension);
        try {
            file.transferTo(target);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to store uploaded file", ex);
        }
        CaseAttachment attachment = new CaseAttachment();
        attachment.setComplaintCase(complaintCase);
        attachment.setOriginalFileName(original);
        attachment.setStoredPath(target.toString());
        attachment.setContentType(contentType);
        attachment.setSizeBytes(file.getSize());
        return attachment;
    }

    public Resource asResource(CaseAttachment attachment) {
        try {
            Path path = Path.of(attachment.getStoredPath()).normalize();
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment file not found");
            }
            return resource;
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment file not found", ex);
        }
    }

    public void delete(CaseAttachment attachment) {
        try {
            Files.deleteIfExists(Path.of(attachment.getStoredPath()).normalize());
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to delete attachment", ex);
        }
    }
}
