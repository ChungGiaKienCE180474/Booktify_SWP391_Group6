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

        @Transactional
        public Rating createRating(
                        Long bookId,
                        Long customerId,
                        Integer ratingValue,
                        String reviewText) {

                // Validate số sao
                if (ratingValue == null || ratingValue < 1 || ratingValue > 5) {
                        throw new IllegalArgumentException(
                                        "Số sao phải từ 1 đến 5.");
                }

                // Validate nội dung
                if (reviewText == null || reviewText.trim().isEmpty()) {
                        throw new IllegalArgumentException(
                                        "Nội dung đánh giá không được để trống.");
                }

                if (reviewText.length() > 1000) {
                        throw new IllegalArgumentException(
                                        "Nội dung đánh giá tối đa 1000 ký tự.");
                }

                // Kiểm tra sách tồn tại
                Book book = bookRepository.findById(bookId)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách."));

                // Kiểm tra customer tồn tại
                User customer = userRepository.findById(customerId)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng."));

                if (customer.getRole() == null
                                || !"CUSTOMER".equals(customer.getRole().getName())) {

                        throw new RuntimeException(
                                        "Chỉ khách hàng mới có thể đánh giá sản phẩm.");

                }

                // Kiểm tra đã mua sách
                boolean purchased = orderRepository
                                .existsByUser_IdAndItems_Book_IdAndStatus(
                                                customerId,
                                                bookId,
                                                "DELIVERED");

                if (!purchased) {
                        throw new RuntimeException(
                                        "Bạn chỉ có thể đánh giá sách đã mua.");
                }

                // Kiểm tra đã đánh giá trước đó
                boolean exists = ratingRepository
                                .existsByBook_IdAndCustomer_Id(
                                                bookId,
                                                customerId);

                if (exists) {
                        throw new RuntimeException(
                                        "Bạn đã đánh giá sách này rồi.");
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

                User customer = userRepository.findById(customerId).orElse(null);

                if (customer == null) {
                        return false;
                }

                if (customer.getRole() == null
                                || !"CUSTOMER".equals(customer.getRole().getName())) {
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

                boolean reviewed = ratingRepository
                                .existsByBook_IdAndCustomer_Id(
                                                bookId,
                                                customerId);

                return !reviewed;
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
}