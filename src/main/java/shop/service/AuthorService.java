package shop.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import shop.domain.Author;
import shop.domain.dto.AuthorDTO;
import shop.repository.AuthorRepository;

@Service
public class AuthorService {

        private final AuthorRepository authorRepository;

        public AuthorService(AuthorRepository authorRepository) {
                this.authorRepository = authorRepository;
        }

        /**
         * Create Author
         */
        public void saveAuthor(AuthorDTO authorDTO) {

                // Trim dữ liệu
                String authorName = authorDTO.getAuthorName().trim();

                String biography = authorDTO.getBiography() == null
                                ? null
                                : authorDTO.getBiography().trim();

                String nationality = authorDTO.getNationality() == null
                                ? null
                                : authorDTO.getNationality().trim();

                String profileImage = authorDTO.getProfileImage() == null
                                ? null
                                : authorDTO.getProfileImage().trim();

                // Mapping DTO -> Entity
                Author author = new Author();

                author.setAuthorName(authorName);
                author.setBiography(biography);
                author.setNationality(nationality);
                author.setProfileImage(profileImage);

                // Auto timestamp
                author.setCreatedAt(LocalDateTime.now());
                author.setUpdatedAt(LocalDateTime.now());

                authorRepository.save(author);
        }

        /**
         * Get all Authors
         */
        public List<Author> getAllAuthors() {
                return authorRepository.findAllByOrderByAuthorIdDesc();
        }

        /**
         * Get active authors for client
         */
        public List<Author> getActiveAuthors() {

                return authorRepository.findAllByStatusTrueOrderByAuthorIdDesc();

        }

        /**
         * Check duplicate author name
         */
        public boolean existsByAuthorNameIgnoreCase(String authorName) {
                return authorRepository.existsByAuthorNameIgnoreCase(authorName.trim());
        }

        public Author getAuthorById(Long id) {
                return authorRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Author not found"));
        }

        /**
         * Resolve Book.author (free-text name) to an active Author record, so the
         * storefront can link to that author's detail page. Empty if no active
         * author matches — Book.author isn't a real foreign key.
         */
        public Optional<Author> findActiveByName(String authorName) {
                if (authorName == null || authorName.isBlank()) return Optional.empty();
                return authorRepository.findByAuthorNameIgnoreCaseAndStatusTrue(authorName.trim());
        }

        /*
         * UPDATE AUTHOR
         */
        public void updateAuthor(Long id, AuthorDTO authorDTO) {

                Author author = authorRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Author not found"));

                // Trim dữ liệu
                String authorName = authorDTO.getAuthorName().trim();

                String biography = authorDTO.getBiography() == null
                                ? null
                                : authorDTO.getBiography().trim();

                String nationality = authorDTO.getNationality() == null
                                ? null
                                : authorDTO.getNationality().trim();

                String profileImage = authorDTO.getProfileImage() == null
                                ? null
                                : authorDTO.getProfileImage().trim();

                // Mapping update
                author.setAuthorName(authorName);
                author.setBiography(biography);
                author.setNationality(nationality);
                author.setProfileImage(profileImage);

                // CHỈ update updatedAt
                author.setUpdatedAt(LocalDateTime.now());

                authorRepository.save(author);
        }

        public boolean existsByAuthorNameIgnoreCaseAndIdNot(String authorName, Long id) {
                return authorRepository.existsByAuthorNameIgnoreCaseAndAuthorIdNot(
                                authorName.trim(), id);
        }

        /**
         * DELETE AUTHOR
         */
        public void deleteAuthor(Long id) {

                Author author = authorRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Author not found"));

                author.setStatus(false);
                author.setUpdatedAt(LocalDateTime.now());

                authorRepository.save(author);
        }

        /**
         * RESTORE AUTHOR (Soft Delete)
         */
        public void restoreAuthor(Long id) {

                Author author = authorRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Author not found"));

                author.setStatus(true);
                author.setUpdatedAt(LocalDateTime.now());

                authorRepository.save(author);
        }
}