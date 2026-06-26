package shop.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class ContactService {

    private final ContactRequestRepository contactRequestRepository;
    private final ContactMessageRepository contactMessageRepository;
    private final ContactAttachmentRepository contactAttachmentRepository;
    private final UserRepository userRepository;

    private final Path uploadRoot = Paths.get(System.getProperty("user.dir"), "uploads", "contact");

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

    public ContactRequest createRequest(
            ContactRequest contactRequest,
            String customerEmail,
            MultipartFile[] images,
            MultipartFile[] files
    ) {
        User customer = getUserByEmail(customerEmail);

        contactRequest.setCustomer(customer);
        contactRequest.setStatus(ContactStatus.OPEN);

        ContactRequest savedRequest = this.contactRequestRepository.save(contactRequest);

        ContactMessage firstMessage = new ContactMessage();
        firstMessage.setContactRequest(savedRequest);
        firstMessage.setSender(customer);
        firstMessage.setMessage(contactRequest.getContent());
        this.contactMessageRepository.save(firstMessage);

        try {
            saveImages(savedRequest, images);
            saveFiles(savedRequest, files);
        } catch (IOException e) {
            throw new RuntimeException("Upload failed: " + e.getMessage());
        }

        return savedRequest;
    }

    public Page<ContactRequest> getCustomerRequests(String customerEmail, Pageable pageable) {
        User customer = getUserByEmail(customerEmail);
        return this.contactRequestRepository.findByCustomerOrderByCreatedAtDesc(customer, pageable);
    }

    public ContactRequest getCustomerRequestDetail(Long id, String customerEmail) {
        User customer = getUserByEmail(customerEmail);

        return this.contactRequestRepository.findByIdAndCustomer(id, customer)
                .orElseThrow(() -> new RuntimeException("Request not found"));
    }

    public ContactRequest getRequestById(Long id) {
        return this.contactRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
    }

    public Page<ContactRequest> searchForStaff(String keyword, ContactStatus status, Pageable pageable) {
        return this.contactRequestRepository.searchForStaff(keyword, status, pageable);
    }

    public List<ContactMessage> getMessages(ContactRequest contactRequest) {
        return this.contactMessageRepository.findByContactRequestOrderBySentAtAsc(contactRequest);
    }

    public List<ContactAttachment> getAttachments(ContactRequest contactRequest) {
        return this.contactAttachmentRepository.findByContactRequest(contactRequest);
    }

    public void sendMessage(Long requestId, String senderEmail, String messageContent) {
        if (messageContent == null || messageContent.trim().isEmpty()) {
            throw new RuntimeException("Message cannot be empty");
        }

        ContactRequest request = getRequestById(requestId);
        User sender = getUserByEmail(senderEmail);

        ContactMessage message = new ContactMessage();
        message.setContactRequest(request);
        message.setSender(sender);
        message.setMessage(messageContent.trim());

        this.contactMessageRepository.save(message);
    }

    public void updateStatus(Long requestId, ContactStatus status, String staffEmail) {
        ContactRequest request = getRequestById(requestId);
        User staff = getUserByEmail(staffEmail);

        request.setStatus(status);
        request.setStaff(staff);

        this.contactRequestRepository.save(request);
    }

    private void saveImages(ContactRequest request, MultipartFile[] images) throws IOException {
        if (images == null || images.length == 0) {
            return;
        }

        for (MultipartFile image : images) {
            if (image == null || image.isEmpty()) {
                continue;
            }

            validateImage(image);

            String filePath = saveFile(image, "images");

            ContactAttachment attachment = new ContactAttachment();
            attachment.setContactRequest(request);
            attachment.setType(ContactAttachmentType.IMAGE);
            attachment.setFileName(image.getOriginalFilename());
            attachment.setFilePath(filePath);

            this.contactAttachmentRepository.save(attachment);
        }
    }

    private void saveFiles(ContactRequest request, MultipartFile[] files) throws IOException {
        if (files == null || files.length == 0) {
            return;
        }

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }

            validateDocument(file);

            String filePath = saveFile(file, "files");

            ContactAttachment attachment = new ContactAttachment();
            attachment.setContactRequest(request);
            attachment.setType(ContactAttachmentType.FILE);
            attachment.setFileName(file.getOriginalFilename());
            attachment.setFilePath(filePath);

            this.contactAttachmentRepository.save(attachment);
        }
    }

    private String saveFile(MultipartFile multipartFile, String folder) throws IOException {
        Path uploadFolder = uploadRoot.resolve(folder);

        if (!Files.exists(uploadFolder)) {
            Files.createDirectories(uploadFolder);
        }

        String originalName = multipartFile.getOriginalFilename();
        String extension = "";

        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }

        String fileName = UUID.randomUUID() + extension;
        Path targetPath = uploadFolder.resolve(fileName);

        Files.copy(multipartFile.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/contact/" + folder + "/" + fileName;
    }

    private User getUserByEmail(String email) {
        User user = this.userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found: " + email);
        }

        return user;
    }

    private void validateImage(MultipartFile image) {
        String contentType = image.getContentType();

        if (contentType == null ||
                !(contentType.equals("image/png")
                        || contentType.equals("image/jpeg")
                        || contentType.equals("image/jpg"))) {
            throw new RuntimeException("Only PNG, JPG, JPEG images are allowed.");
        }

        if (image.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("Each image must be less than 5MB.");
        }
    }

    private void validateDocument(MultipartFile file) {
        String fileName = file.getOriginalFilename();

        if (fileName == null) {
            throw new RuntimeException("Invalid file.");
        }

        String lowerName = fileName.toLowerCase();

        if (!(lowerName.endsWith(".pdf")
                || lowerName.endsWith(".doc")
                || lowerName.endsWith(".docx")
                || lowerName.endsWith(".txt"))) {
            throw new RuntimeException("Only PDF, DOC, DOCX, TXT files are allowed.");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("Each file must be less than 10MB.");
        }
    }

    public long countAllRequests() {
        return this.contactRequestRepository.count();
    }

    public long countRequestsByStatus(ContactStatus status) {
        return this.contactRequestRepository.countByStatus(status);
    }
}