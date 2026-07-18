package shop.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import shop.domain.Book;
import shop.domain.Rating;
import shop.domain.User;
import shop.repository.BookRepository;
import shop.repository.OrderRepository;
import shop.repository.RatingRepository;
import shop.repository.UserRepository;
import java.util.List;

@Service
public class RatingService {

        private final RatingRepository ratingRepository;
        private final BookRepository bookRepository;
        private final UserRepository userRepository;
        private final OrderRepository orderRepository;

        public RatingService(
                        RatingRepository ratingRepository,
                        BookRepository bookRepository,
                        UserRepository userRepository,
                        OrderRepository orderRepository) {

                this.ratingRepository = ratingRepository;
                this.bookRepository = bookRepository;
                this.userRepository = userRepository;
                this.orderRepository = orderRepository;
        }

        public List<Book> getBooksHasReview() {
                return ratingRepository.findAll()
                                .stream()
                                .map(Rating::getBook)
                                .distinct()
                                .toList();

        }

        public List<Rating> getRatingsByBookForAdmin(Long bookId) {
                return ratingRepository.findByBook_Id(bookId);
        }

        public Book getBook(Long bookId) {
                return bookRepository.findById(bookId)
                                .orElseThrow(() -> new RuntimeException("Book not found."));
        }

        @Transactional
        public Rating createRating(
                        Long bookId,
                        Long customerId,
                        Integer ratingValue,
                        String reviewText) {

                // Validate số sao
                if (ratingValue == null || ratingValue < 1 || ratingValue > 5) {
                        throw new IllegalArgumentException(
                                        "The number of stars must be between 1 and 5.");
                }

                // Validate nội dung
                if (reviewText == null || reviewText.trim().isEmpty()) {
                        throw new IllegalArgumentException(
                                        "The evaluation section must not be left blank.");
                }

                if (reviewText.length() > 1000) {
                        throw new IllegalArgumentException(
                                        "The review content should be a maximum of 1000 characters.");
                }

                // Kiểm tra sách tồn tại
                Book book = bookRepository.findById(bookId)
                                .orElseThrow(() -> new RuntimeException("Book not found."));

                // Kiểm tra customer tồn tại
                User customer = userRepository.findById(customerId)
                                .orElseThrow(() -> new RuntimeException("No customer found."));

                if (customer.getRole() == null
                                || !"CUSTOMER".equals(customer.getRole().getName())) {

                        throw new RuntimeException(
                                        "Only customers can rate the product.");

                }

                // Kiểm tra đã mua sách
                boolean purchased = orderRepository
                                .existsByUser_IdAndItems_Book_IdAndStatus(
                                                customerId,
                                                bookId,
                                                "DELIVERED");
                if (!purchased) {
                        throw new RuntimeException(
                                        "You can only rate books you have purchased.");
                }
                // Kiểm tra đã đánh giá trước đó
                Optional<Rating> oldRating = ratingRepository.findByBook_IdAndCustomer_Id(
                                bookId,
                                customerId);
                if (oldRating.isPresent()) {
                        Rating rating = oldRating.get();
                        if ("DELETED".equals(rating.getStatus())) {
                                rating.setRatingValue(ratingValue);
                                rating.setReview(reviewText.trim());
                                rating.setStatus("ACTIVE");
                                return ratingRepository.save(rating);
                        }
                        throw new RuntimeException(
                                        "You have already reviewed this book.");
                }

                // Tạo rating
                Rating rating = new Rating();

                rating.setBook(book);
                rating.setCustomer(customer);
                rating.setRatingValue(ratingValue);
                rating.setReview(reviewText.trim());
                rating.setStatus("ACTIVE");

                return ratingRepository.save(rating);
        }

        public boolean canCustomerReview(Long bookId, Long customerId) {
                User customer = userRepository.findById(customerId)
                                .orElse(null);
                if (customer == null) {
                        return false;
                }

                boolean purchased = orderRepository
                                .existsByUser_IdAndItems_Book_IdAndStatus(
                                                customerId,
                                                bookId,
                                                "DELIVERED");
                if (!purchased) {
                        return false;
                }

                Optional<Rating> rating = ratingRepository.findByBook_IdAndCustomer_Id(bookId, customerId);

                if (rating.isEmpty()) {
                        return true;
                }
                // Nếu khách đã xóa review
                if ("DELETED".equals(rating.get().getStatus())) {
                        return true;
                }
                // ACTIVE hoặc HIDDEN đều tính là đã review
                return false;
        }

        public Optional<Rating> getCustomerRating(
                        Long bookId,
                        Long customerId) {

                return ratingRepository
                                .findByBook_IdAndCustomer_Id(
                                                bookId,
                                                customerId);
        }

        public List<Rating> getRatingsByBook(Long bookId) {
                return ratingRepository.findByBook_IdAndStatus(
                                bookId,
                                "ACTIVE");
        }

        @Transactional
        public Rating updateRating(
                        Long bookId,
                        Long customerId,
                        Integer ratingValue,
                        String reviewText) {

                Rating rating = ratingRepository
                                .findByBook_IdAndCustomer_Id(
                                                bookId,
                                                customerId)
                                .orElseThrow(() -> new RuntimeException("No reviews found."));

                if (ratingValue < 1 || ratingValue > 5) {
                        throw new IllegalArgumentException("The number of stars must be between 1 and 5.");
                }

                if (reviewText == null || reviewText.trim().isEmpty()) {
                        throw new IllegalArgumentException("The field must not be left blank.");
                }

                if (reviewText.length() > 1000) {
                        throw new IllegalArgumentException(
                                        "The review content should be a maximum of 1000 characters.");
                }

                rating.setRatingValue(ratingValue);
                rating.setReview(reviewText.trim());
                rating.setStatus("ACTIVE");

                return ratingRepository.save(rating);
        }

        // DELETE
        @Transactional
        public void deleteRating(Long bookId, Long customerId) {

                Rating rating = ratingRepository
                                .findByBook_IdAndCustomer_IdAndStatus(
                                                bookId,
                                                customerId,
                                                "ACTIVE")
                                .orElseThrow(() -> new RuntimeException("Rating not found."));

                rating.setStatus("DELETED");

                // khách hàng tự xóa
                rating.setDeletedBy("CUSTOMER");

                ratingRepository.save(rating);
        }

        public Long getReviewCount(Long bookId) {

                return (long) ratingRepository
                                .findByBook_IdAndStatus(bookId, "ACTIVE")
                                .size();
        }

        public Double getAverageRating(Long bookId) {

                List<Rating> ratings = ratingRepository.findByBook_IdAndStatus(
                                bookId,
                                "ACTIVE");

                if (ratings.isEmpty()) {
                        return 0.0;
                }

                return ratings.stream()
                                .mapToInt(Rating::getRatingValue)
                                .average()
                                .orElse(0.0);
        }

        @Transactional
        public void hideRating(Integer ratingId) {
                Rating rating = ratingRepository.findById(ratingId)
                                .orElseThrow(() -> new RuntimeException("Rating not found."));
                rating.setStatus("HIDDEN");
                // admin ẩn
                rating.setDeletedBy("ADMIN");
                ratingRepository.save(rating);
        }

        @Transactional
        public void visibleRating(Integer ratingId) {
                Rating rating = ratingRepository.findById(ratingId)
                                .orElseThrow(() -> new RuntimeException("Rating not found."));
                // chỉ restore review do ADMIN hide
                if (!"ADMIN".equals(rating.getDeletedBy())) {
                        throw new RuntimeException(
                                        "Customer deleted review cannot be restored.");
                }
                rating.setStatus("ACTIVE");
                rating.setDeletedBy(null);
                ratingRepository.save(rating);
        }

        public List<Book> getBooksHasReview(String keyword) {

                if (keyword == null) {
                        keyword = "";
                }

                return ratingRepository.findBooksHasReview(keyword.trim());
        }
}