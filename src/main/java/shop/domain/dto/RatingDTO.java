package shop.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RatingDTO {

    @NotNull
    private Integer bookId;

    @NotNull
    private Long customerId;

    @NotNull(message = "Vui lòng chọn số sao.")
    @Min(value = 1, message = "Số sao phải từ 1 đến 5.")
    @Max(value = 5, message = "Số sao phải từ 1 đến 5.")
    private Integer ratingValue;

    @NotBlank(message = "Vui lòng nhập nội dung đánh giá.")
    @Size(max = 1000, message = "Nội dung đánh giá tối đa 1000 ký tự.")
    private String review;

    public RatingDTO() {
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Integer getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(Integer ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}