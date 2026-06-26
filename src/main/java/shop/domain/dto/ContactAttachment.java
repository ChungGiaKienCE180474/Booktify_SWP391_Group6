package shop.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "contact_attachments")
public class ContactAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contact_request_id", nullable = false)
    private ContactRequest contactRequest;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContactAttachmentType type;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    public boolean isImage() {
        return this.type == ContactAttachmentType.IMAGE;
    }

    public boolean isFile() {
        return this.type == ContactAttachmentType.FILE;
    }

    public Long getId() {
        return id;
    }

    public ContactRequest getContactRequest() {
        return contactRequest;
    }

    public void setContactRequest(ContactRequest contactRequest) {
        this.contactRequest = contactRequest;
    }

    public ContactAttachmentType getType() {
        return type;
    }

    public void setType(ContactAttachmentType type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}