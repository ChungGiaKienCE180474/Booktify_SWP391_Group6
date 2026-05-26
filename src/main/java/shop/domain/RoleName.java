package shop.domain;

public enum RoleName {
    CUSTOMER("Customer"),
    ADMIN("Administrator"),
    STAFF("Staff");

    private final String description;

    RoleName(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
