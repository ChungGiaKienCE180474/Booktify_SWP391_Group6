package shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.domain.ContactMessage;
import shop.domain.ContactRequest;

import java.util.List;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

    List<ContactMessage> findByContactRequestOrderBySentAtAsc(ContactRequest contactRequest);
}