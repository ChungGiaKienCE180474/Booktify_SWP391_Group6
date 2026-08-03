package shop.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "contact_requests")
public class ContactRequest {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private User staff;

    @Column(name = "requester_type", length = 30)
    private String requesterType;

    @Column(name = "issue_type", length = 120)
    private String issueType;

    @Column(name = "contact_email", length = 150)
    private String contactEmail;

    @Column(name = "order_code", length = 50)
    private String orderCode;

    @Column(nullable = false, length = 255)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "consent_accepted")
    private Boolean consentAccepted = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ContactStatus status = ContactStatus.OPEN;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "contactRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sentAt ASC")
    private List<ContactMessage> messages;

    @OneToMany(mappedBy = "contactRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContactAttachment> attachments;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }

        if (this.status == null) {
            this.status = ContactStatus.OPEN;
        }

        if (this.consentAccepted == null) {
            this.consentAccepted = false;
        }
    }

    public String getDisplayId() {
        return this.id == null ? "" : String.valueOf(this.id);
    }

    public String getCreatedAtFormatted() {
        if (this.createdAt == null) {
            return "—";
        }

        return this.createdAt.format(DATE_TIME_FORMATTER);
    }

    public String getFormattedCreatedAt() {
        return getCreatedAtFormatted();
    }

    public String getDisplayCreatedAt() {
        return getCreatedAtFormatted();
    }

    public String getStatusText() {
        if (this.status == null) {
            return "Open";
        }

        return switch (this.status) {
            case OPEN -> "Open";
            case IN_PROGRESS -> "In Progress";
            case COMPLETE -> "Completed";
        };
    }

    public Long getId() {
        return id;
    }

    public User getCustomer() {
        return customer;
    }

    public User getStaff() {
        return staff;
    }

    public String getRequesterType() {
        return requesterType;
    }

    public String getIssueType() {
        return issueType;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public Boolean getConsentAccepted() {
        return consentAccepted;
    }

    public ContactStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<ContactMessage> getMessages() {
        return messages;
    }

    public List<ContactAttachment> getAttachments() {
        return attachments;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public void setStaff(User staff) {
        this.staff = staff;
    }

    public void setRequesterType(String requesterType) {
        this.requesterType = requesterType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setConsentAccepted(Boolean consentAccepted) {
        this.consentAccepted = consentAccepted;
    }

    public void setStatus(ContactStatus status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setMessages(List<ContactMessage> messages) {
        this.messages = messages;
    }

    public void setAttachments(List<ContactAttachment> attachments) {
        this.attachments = attachments;
    }
  
}