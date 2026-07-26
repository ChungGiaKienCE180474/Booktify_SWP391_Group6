package shop.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import shop.domain.ContactAttachment;
import shop.domain.ContactAttachmentType;
import shop.domain.ContactMessage;
import shop.domain.ContactRequest;
import shop.domain.ContactStatus;
import shop.domain.User;
import shop.repository.ContactAttachmentRepository;
import shop.repository.ContactMessageRepository;
import shop.repository.ContactRequestRepository;
import shop.repository.UserRepository;

@Service
public class ContactService {

    private final ContactRequestRepository contactRequestRepository;
    private final ContactMessageRepository contactMessageRepository;
    private final ContactAttachmentRepository contactAttachmentRepository;
    private final UserRepository userRepository;

    @Value("${app.upload.dir:${user.home}/booktify-uploads}")
    private String uploadDir;

    public ContactService(
            ContactRequestRepository contactRequestRepository,
            ContactMessageRepository contactMessageRepository,
            ContactAttachmentRepository contactAttachmentRepository,
            UserRepository userRepository
    ) {
        this.contactRequestRepository = contactRequestRepository;
        this.contactMessageRepository = contactMessageRepository;
        this.contactAttachmentRepository = contactAttachmentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ContactRequest createRequest(
            ContactRequest contactRequest,
            String customerEmail,
            MultipartFile[] attachmentFiles
    ) {
        User customer = getUserByEmail(customerEmail);

        validateRequest(contactRequest);

        contactRequest.setCustomer(customer);
        contactRequest.setStatus(ContactStatus.OPEN);

        contactRequest.setIssueType(clean(contactRequest.getIssueType()));
        contactRequest.setContactEmail(clean(contactRequest.getContactEmail()));
        contactRequest.setContent(clean(contactRequest.getContent()));

        if (contactRequest.getSubject() == null || contactRequest.getSubject().isBlank()) {
            contactRequest.setSubject(buildSubject(contactRequest));
        } else {
            contactRequest.setSubject(clean(contactRequest.getSubject()));
        }

        ContactRequest savedRequest = this.contactRequestRepository.save(contactRequest);

        ContactMessage firstMessage = new ContactMessage();
        firstMessage.setContactRequest(savedRequest);
        firstMessage.setSender(customer);
        firstMessage.setMessage(savedRequest.getContent());
        this.contactMessageRepository.save(firstMessage);

        try {
            saveAttachments(savedRequest, attachmentFiles);
        } catch (IOException exception) {
            throw new RuntimeException("Upload failed: " + exception.getMessage(), exception);
        }

        return savedRequest;
    }

    @Transactional
    public ContactRequest createRequest(
            ContactRequest contactRequest,
            String customerEmail,
            MultipartFile[] images,
            MultipartFile[] files
    ) {
        MultipartFile[] mergedFiles = mergeFiles(images, files);
        return createRequest(contactRequest, customerEmail, mergedFiles);
    }

    @Transactional(readOnly = true)
    public Page<ContactRequest> getCustomerRequests(
            String customerEmail,
            ContactStatus status,
            Pageable pageable
    ) {
        User customer = getUserByEmail(customerEmail);

        if (status == null) {
            return this.contactRequestRepository.findByCustomerOrderByCreatedAtDesc(
                    customer,
                    pageable
            );
        }

        return this.contactRequestRepository.findByCustomerAndStatusOrderByCreatedAtDesc(
                customer,
                status,
                pageable
        );
    }

    @Transactional(readOnly = true)
    public Page<ContactRequest> getCustomerRequests(
            String customerEmail,
            Pageable pageable
    ) {
        return getCustomerRequests(customerEmail, null, pageable);
    }

    @Transactional(readOnly = true)
    public ContactRequest getCustomerRequestDetail(Long id, String customerEmail) {
        User customer = getUserByEmail(customerEmail);

        return this.contactRequestRepository.findByIdAndCustomer(id, customer)
                .orElseThrow(() -> new RuntimeException("Request not found."));
    }

    @Transactional(readOnly = true)
    public ContactRequest getRequestById(Long id) {
        return this.contactRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found."));
    }

    @Transactional(readOnly = true)
    public Page<ContactRequest> searchForStaff(
            String keyword,
            ContactStatus status,
            Pageable pageable
    ) {
        return this.contactRequestRepository.searchForStaff(
                clean(keyword),
                status,
                pageable
        );
    }

    @Transactional(readOnly = true)
    public List<ContactMessage> getMessages(ContactRequest contactRequest) {
        return this.contactMessageRepository.findByContactRequestOrderBySentAtAsc(contactRequest);
    }

    @Transactional(readOnly = true)
    public List<ContactAttachment> getAttachments(ContactRequest contactRequest) {
        return this.contactAttachmentRepository.findByContactRequest(contactRequest);
    }

    @Transactional
    public void sendMessage(Long requestId, String senderEmail, String messageContent) {
        if (messageContent == null || messageContent.trim().isEmpty()) {
            throw new RuntimeException("Message cannot be empty.");
        }

        ContactRequest request = getRequestById(requestId);
        User sender = getUserByEmail(senderEmail);

        ContactMessage message = new ContactMessage();
        message.setContactRequest(request);
        message.setSender(sender);
        message.setMessage(messageContent.trim());

        this.contactMessageRepository.save(message);
    }

    @Transactional
    public void updateStatus(Long requestId, ContactStatus status, String staffEmail) {
        ContactRequest request = getRequestById(requestId);
        User staff = getUserByEmail(staffEmail);

        request.setStatus(status);
        request.setStaff(staff);

        this.contactRequestRepository.save(request);
    }

    @Transactional(readOnly = true)
    public long countAllRequests() {
        return this.contactRequestRepository.count();
    }

    @Transactional(readOnly = true)
    public long countRequestsByStatus(ContactStatus status) {
        return this.contactRequestRepository.countByStatus(status);
    }

    private void validateRequest(ContactRequest request) {
        if (request.getIssueType() == null || request.getIssueType().isBlank()) {
            throw new RuntimeException("Please select your issue type.");
        }

        if (request.getContactEmail() == null || request.getContactEmail().isBlank()) {
            throw new RuntimeException("Email is required.");
        }

        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new RuntimeException("Content is required.");
        }

        if (!Boolean.TRUE.equals(request.getConsentAccepted())) {
            throw new RuntimeException("You must agree to the privacy policy before submitting.");
        }
    }

    private String buildSubject(ContactRequest request) {
        if (request.getIssueType() == null || request.getIssueType().isBlank()) {
            return "Support request";
        }

        return request.getIssueType().trim();
    }

    private void saveAttachments(
            ContactRequest request,
            MultipartFile[] attachmentFiles
    ) throws IOException {
        if (attachmentFiles == null || attachmentFiles.length == 0) {
            return;
        }

        for (MultipartFile attachmentFile : attachmentFiles) {
            if (attachmentFile == null || attachmentFile.isEmpty()) {
                continue;
            }

            validateAttachment(attachmentFile);

            String filePath = saveFile(attachmentFile);

            ContactAttachmentType type = isImageFile(attachmentFile)
                    ? ContactAttachmentType.IMAGE
                    : ContactAttachmentType.FILE;

            ContactAttachment attachment = new ContactAttachment();
            attachment.setContactRequest(request);
            attachment.setType(type);
            attachment.setFileName(attachmentFile.getOriginalFilename());
            attachment.setFilePath(filePath);

            this.contactAttachmentRepository.save(attachment);
        }
    }

    private String saveFile(MultipartFile multipartFile) throws IOException {
        Path uploadRoot = getUploadRoot();

        Files.createDirectories(uploadRoot);

        String originalName = multipartFile.getOriginalFilename();
        String extension = "";

        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
        }

        String fileName = UUID.randomUUID() + extension;

        Path targetPath = uploadRoot.resolve(fileName)
                .toAbsolutePath()
                .normalize();

        if (!targetPath.startsWith(uploadRoot)) {
            throw new RuntimeException("Invalid upload path.");
        }

        Files.copy(
                multipartFile.getInputStream(),
                targetPath,
                StandardCopyOption.REPLACE_EXISTING
        );

        return "/uploads/contact/attachments/" + fileName;
    }

    private Path getUploadRoot() {
        return Paths.get(uploadDir, "contact", "attachments")
                .toAbsolutePath()
                .normalize();
    }

    private void validateAttachment(MultipartFile file) {
        String originalName = file.getOriginalFilename();

        if (originalName == null || originalName.isBlank()) {
            throw new RuntimeException("Invalid attachment.");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("Each attachment must be less than 10MB.");
        }

        String lowerName = originalName.toLowerCase();

        boolean allowed = lowerName.endsWith(".png")
                || lowerName.endsWith(".jpg")
                || lowerName.endsWith(".jpeg")
                || lowerName.endsWith(".webp")
                || lowerName.endsWith(".pdf")
                || lowerName.endsWith(".doc")
                || lowerName.endsWith(".docx")
                || lowerName.endsWith(".xls")
                || lowerName.endsWith(".xlsx")
                || lowerName.endsWith(".txt")
                || lowerName.endsWith(".zip");

        if (!allowed) {
            throw new RuntimeException(
                    "Only PNG, JPG, JPEG, WEBP, PDF, DOC, DOCX, XLS, XLSX, TXT, and ZIP files are allowed."
            );
        }
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();

        if (contentType != null && contentType.toLowerCase().startsWith("image/")) {
            return true;
        }

        if (fileName == null) {
            return false;
        }

        String lowerName = fileName.toLowerCase();

        return lowerName.endsWith(".png")
                || lowerName.endsWith(".jpg")
                || lowerName.endsWith(".jpeg")
                || lowerName.endsWith(".webp");
    }

    private MultipartFile[] mergeFiles(
            MultipartFile[] first,
            MultipartFile[] second
    ) {
        int firstLength = first == null ? 0 : first.length;
        int secondLength = second == null ? 0 : second.length;

        MultipartFile[] result = new MultipartFile[firstLength + secondLength];

        if (firstLength > 0) {
            System.arraycopy(first, 0, result, 0, firstLength);
        }

        if (secondLength > 0) {
            System.arraycopy(second, 0, result, firstLength, secondLength);
        }

        return result;
    }

    private User getUserByEmail(String email) {
        User user = this.userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found: " + email);
        }

        return user;
    }

    private String clean(String value) {
        return value == null ? null : value.trim();
    }
}