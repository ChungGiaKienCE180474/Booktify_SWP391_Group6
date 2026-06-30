package shop.service;

import java.time.LocalDateTime;
import java.util.List;

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
         * Check duplicate author name
         */
        public boolean existsByAuthorNameIgnoreCase(String authorName) {
                return authorRepository.existsByAuthorNameIgnoreCase(authorName.trim());
        }

        public Author getAuthorById(Long id) {
                return authorRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Author not found"));
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

                authorRepository.delete(author);
        }
}