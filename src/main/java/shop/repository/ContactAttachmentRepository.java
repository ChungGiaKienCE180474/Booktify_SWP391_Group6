package shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.domain.ContactAttachment;
import shop.domain.ContactRequest;

import java.util.List;

public interface ContactAttachmentRepository extends JpaRepository<ContactAttachment, Long> {

    List<ContactAttachment> findByContactRequest(ContactRequest contactRequest);
}