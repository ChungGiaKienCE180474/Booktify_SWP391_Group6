package shop.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Table(name = "contact_requests")
public class ContactRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Customer tạo yêu cầu hỗ trợ
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    // Staff xử lý yêu cầu, ban đầu có thể null
    @ManyToOne
    @JoinColumn(name = "staff_id")
    private User staff;

    @Column(name = "order_code", length = 50)
    private String orderCode;

    @Column(nullable = false, length = 255)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // Các cột cũ này có thể giữ lại, nhưng chức năng nhiều file sẽ dùng ContactAttachment
    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_name")
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ContactStatus status = ContactStatus.OPEN;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "contactRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContactMessage> messages;

    @OneToMany(mappedBy = "contactRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContactAttachment> attachments;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = ContactStatus.OPEN;
        }
    }

    // ID hiển thị: 1, 2, 3...
    public String getDisplayId() {
        if (this.id == null) {
            return "";
        }

        return String.valueOf(this.id);
    }

    // Format ngày: 18/06/2026 18:55
    public String getFormattedCreatedAt() {
        if (this.createdAt == null) {
            return "";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return this.createdAt.format(formatter);
    }

    public String getDisplayCreatedAt() {
        return getFormattedCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public User getStaff() {
        return staff;
    }

    public void setStaff(User staff) {
        this.staff = staff;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ContactStatus getStatus() {
        return status;
    }

    public void setStatus(ContactStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<ContactMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ContactMessage> messages) {
        this.messages = messages;
    }

    public List<ContactAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<ContactAttachment> attachments) {
        this.attachments = attachments;
    }
}