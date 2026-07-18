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

    @NotNull(message = "Please select the number of stars.")
    @Min(value = 1, message = "The number of stars must be between 1 and 5.")
    @Max(value = 5, message = "The number of stars must be between 1 and 5.")
    private Integer ratingValue;

    @NotBlank(message = "Please enter your review.")
    @Size(max = 1000, message = "The review content should be a maximum of 1000 characters.")
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