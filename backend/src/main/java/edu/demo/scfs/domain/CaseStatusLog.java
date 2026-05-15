package edu.demo.scfs.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "case_status_logs")
public class CaseStatusLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private ComplaintCase complaintCase;

    @Enumerated(EnumType.STRING)
    private CaseStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseStatus newStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id")
    private AppUser operator;

    @Column(length = 1200)
    private String note;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ComplaintCase getComplaintCase() {
        return complaintCase;
    }

    public void setComplaintCase(ComplaintCase complaintCase) {
        this.complaintCase = complaintCase;
    }

    public CaseStatus getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(CaseStatus oldStatus) {
        this.oldStatus = oldStatus;
    }

    public CaseStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(CaseStatus newStatus) {
        this.newStatus = newStatus;
    }

    public AppUser getOperator() {
        return operator;
    }

    public void setOperator(AppUser operator) {
        this.operator = operator;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
