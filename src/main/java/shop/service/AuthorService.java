package shop.service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import shop.domain.Author;
import shop.domain.dto.AuthorDTO;
import shop.repository.AuthorRepository;

import java.nio.file.Path;

@Service
public class AuthorService {

        private String uploadDir = "src/main/resources/images/authors/";

        private final AuthorRepository authorRepository;

        public AuthorService(AuthorRepository authorRepository) {
                this.authorRepository = authorRepository;
        }

        /**
         * Create Author
         */
        public void saveAuthor(AuthorDTO authorDTO, MultipartFile imageFile) {

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
                author.setProfileImage(uploadImage(imageFile));

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
                if (authorName == null || authorName.isBlank())
                        return Optional.empty();
                return authorRepository.findByAuthorNameIgnoreCaseAndStatusTrue(authorName.trim());
        }

        /*
         * UPDATE AUTHOR
         */
        public void updateAuthor(Long id, AuthorDTO authorDTO, MultipartFile imageFile) {

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
                if (imageFile != null && !imageFile.isEmpty()) {
                        author.setProfileImage(uploadImage(imageFile));
                }

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

        private String uploadImage(MultipartFile file) {

                if (file == null || file.isEmpty()) {
                        return null;
                }

                try {

                        Path folder = Paths.get(uploadDir);

                        Files.createDirectories(folder);

                        String extension = "";

                        String originalName = file.getOriginalFilename();

                        if (originalName != null && originalName.contains(".")) {
                                extension = originalName.substring(
                                                originalName.lastIndexOf("."));
                        }

                        String fileName = System.currentTimeMillis() + extension;

                        Path path = folder.resolve(fileName);

                        Files.copy(
                                        file.getInputStream(),
                                        path);

                        return "/images/authors/" + fileName;

                } catch (Exception e) {

                        e.printStackTrace();

                        throw new RuntimeException("Upload image failed");
                }
        }

        public List<Author> searchAuthors(String keyword, String status) {

                String key = keyword == null ? "" : keyword.trim();

                // Không filter gì
                if (key.isEmpty() && (status == null || status.isBlank())) {
                        return authorRepository.findAllByOrderByAuthorIdDesc();
                }

                // Chỉ search
                if (status == null || status.isBlank()) {
                        return authorRepository
                                        .findByAuthorNameContainingIgnoreCaseOrNationalityContainingIgnoreCaseOrderByAuthorIdDesc(
                                                        key,
                                                        key);
                }

                boolean active = status.equalsIgnoreCase("active");

                // Search + Status
                return authorRepository
                                .findByStatusAndAuthorNameContainingIgnoreCaseOrStatusAndNationalityContainingIgnoreCaseOrderByAuthorIdDesc(
                                                active,
                                                key,
                                                active,
                                                key);
        }

}