package shop.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthorDTO {

    private Long authorId;

    @NotBlank(message = "Author Name is required.")
    @Size(max = 100, message = "Author Name must not exceed 100 characters.")
    private String authorName;

    @NotBlank(message = "Biography is required.")
    @Size(max = 3000, message = "Biography must not exceed 3000 characters.")
    private String biography;

    @NotBlank(message = "Nationality is required.")
    @Size(max = 50, message = "Nationality must not exceed 50 characters.")
    private String nationality;

    @Size(max = 1000, message = "Profile Image must not exceed 1000 characters.")
    private String profileImage;

    public AuthorDTO() {
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}