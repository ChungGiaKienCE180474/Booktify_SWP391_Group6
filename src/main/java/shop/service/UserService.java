package shop.service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.domain.dto.StaffDTO;
import shop.domain.Role;
import shop.domain.RoleName;
import shop.domain.AuthProvider;
import shop.domain.User;
import shop.domain.dto.CustomerDTO;
import shop.domain.dto.ProfileDTO;
import shop.domain.dto.RegisterDTO;
import shop.repository.RoleRepository;
import shop.repository.UserRepository;

@Service
public class UserService {

    private static final int MAX_NAME_LENGTH = 150;
    private static final int MAX_ADDRESS_LENGTH = 500;
    private static final String PHONE_PATTERN = "^(0[35789])[0-9]{8}$";
    private static final String PASSWORD_PATTERN =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,72}$";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerDTOtoUser(RegisterDTO registerDTO) {
        User user = new User();
        user.setFullName(registerDTO.getFirstName() + " " + registerDTO.getLastName());
        user.setEmail(normalizeEmail(registerDTO.getEmail()));
        return user;
    }

    @Transactional
    public User registerNewUser(RegisterDTO registerDTO, RoleName roleName) {
        if (registerDTO == null) {
            throw new IllegalArgumentException("Registration information is required.");
        }

        String normalizedEmail = normalizeEmail(registerDTO.getEmail());
        if (normalizedEmail == null || normalizedEmail.isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException(
                    "This email address is already used by another customer or staff account."
            );
        }

        User user = registerDTOtoUser(registerDTO);
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setRole(getRoleByName(roleName));
        user.setStatus(true);
        user.setDeleted(false);
        user.setAuthProvider(AuthProvider.LOCAL.name());
        return userRepository.save(user);
    }

    public User processOAuth2User(String email, String fullName, String avatarUrl) {
        User user = getUserByEmail(email);
        if (user != null) {
            boolean updated = false;
            if (avatarUrl != null && (user.getAvatar() == null || user.getAvatar().isBlank())) {
                user.setAvatar(avatarUrl);
                updated = true;
            }
            if (updated) {
                return userRepository.save(user);
            }
            return user;
        }

        user = new User();
        user.setEmail(normalizeEmail(email));
        user.setFullName(fullName != null && !fullName.isBlank() ? fullName.trim() : normalizeEmail(email));
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setAvatar(avatarUrl);
        user.setRole(getRoleByName(RoleName.CUSTOMER));
        user.setStatus(true);
        user.setDeleted(false);
        user.setAuthProvider(AuthProvider.GOOGLE.name());
        return userRepository.save(user);
    }

    public User handleSaveUser(User user) {
        if (user.getEmail() != null) {
            user.setEmail(normalizeEmail(user.getEmail()));
        }
        return userRepository.save(user);
    }

    public String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    public Role getRoleByName(RoleName roleName) {
        return roleRepository.findByName(roleName.name());
    }

    public boolean checkEmailExist(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return userRepository.existsByEmailIgnoreCase(normalizeEmail(email));
    }

    public User getUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return userRepository.findByEmailIgnoreCase(normalizeEmail(email));
    }

    public ProfileDTO getProfileDTOByEmail(String email) {
        User user = getUserByEmail(email);
        return user != null ? toProfileDTO(user) : null;
    }

    public ProfileDTO toProfileDTO(User user) {
        if (user == null) {
            return null;
        }
        ProfileDTO dto = new ProfileDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setAvatar(user.getAvatar());
        dto.setStatus(user.isStatus());
        if (user.getRole() != null) {
            dto.setRoleName(user.getRole().getName());
        }
        dto.setAuthProvider(user.getAuthProvider());
        return dto;
    }

    public void updatePassword(String email, String plainPassword) {
        User user = getUserByEmail(email);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(plainPassword));
            if (user.isGoogleAccount()) {
                user.setAuthProvider(AuthProvider.LOCAL.name());
            }
            userRepository.save(user);
        }
    }

    public User updateProfile(String email, String fullName, String phone, String address) {
        User user = getUserByEmail(email);
        if (user == null) {
            return null;
        }
        user.setFullName(fullName != null ? fullName.trim() : null);
        user.setPhone(blankToNull(phone));
        user.setAddress(blankToNull(address));
        return userRepository.save(user);
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    // ================= CUSTOMER MANAGEMENT =================

    private boolean isCustomer(User user) {
        return user.getRole() != null
                && "CUSTOMER".equalsIgnoreCase(
                user.getRole().getName()
        );
    }

    private CustomerDTO toCustomerDTO(User user) {
        return new CustomerDTO(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getAddress(),
                user.isStatus(),
                generateCustomerCode(user.getId())
        );
    }

    private String generateCustomerCode(Long id) {
        return String.format(
                "CUS-%06d",
                Math.abs(
                        (id * 7919 + 104729)
                                % 1000000
                )
        );
    }

    public Page<CustomerDTO> getCustomersPage(
            String keyword,
            String status,
            Pageable pageable) {

        String normalizedKeyword =
                keyword == null
                        ? ""
                        : keyword.trim().toLowerCase();

        List<CustomerDTO> customers =
                userRepository.findAll()
                        .stream()
                        .filter(this::isCustomer)
                        .filter(user -> !user.isDeleted())
                        .filter(user -> {

                            if (normalizedKeyword.isEmpty()) {
                                return true;
                            }

                            return containsIgnoreCase(
                                    user.getEmail(),
                                    normalizedKeyword
                            )
                                    || containsIgnoreCase(
                                    user.getFullName(),
                                    normalizedKeyword
                            )
                                    || containsIgnoreCase(
                                    user.getPhone(),
                                    normalizedKeyword
                            )
                                    || containsIgnoreCase(
                                    user.getAddress(),
                                    normalizedKeyword
                            );
                        })
                        .filter(user -> {

                            if (status == null
                                    || "all".equalsIgnoreCase(status)) {
                                return true;
                            }

                            if ("active".equalsIgnoreCase(status)) {
                                return user.isStatus();
                            }

                            if ("inactive".equalsIgnoreCase(status)) {
                                return !user.isStatus();
                            }

                            return true;
                        })
                        .map(this::toCustomerDTO)
                        .collect(Collectors.toList());

        int start =
                (int) pageable.getOffset();

        int end =
                Math.min(
                        start + pageable.getPageSize(),
                        customers.size()
                );

        List<CustomerDTO> pageContent =
                start >= customers.size()
                        ? List.of()
                        : customers.subList(start, end);

        return new PageImpl<>(
                pageContent,
                pageable,
                customers.size()
        );
    }

    private boolean containsIgnoreCase(
            String value,
            String normalizedKeyword) {

        return value != null
                && value.toLowerCase()
                .contains(normalizedKeyword);
    }

    public CustomerDTO getCustomerDTOById(Long id) {

        if (id == null) {
            return null;
        }

        return userRepository.findById(id)
                .filter(this::isCustomer)
                .filter(user -> !user.isDeleted())
                .map(this::toCustomerDTO)
                .orElse(null);
    }

    public long countCustomers() {

        return userRepository.findAll()
                .stream()
                .filter(this::isCustomer)
                .filter(user -> !user.isDeleted())
                .count();
    }

    public long countActiveCustomers() {

        return userRepository.findAll()
                .stream()
                .filter(this::isCustomer)
                .filter(user -> !user.isDeleted())
                .filter(User::isStatus)
                .count();
    }

    public long countInactiveCustomers() {

        return userRepository.findAll()
                .stream()
                .filter(this::isCustomer)
                .filter(user -> !user.isDeleted())
                .filter(user -> !user.isStatus())
                .count();
    }

    public void banUser(Long userId) {

        userRepository.findById(userId)
                .filter(this::isCustomer)
                .filter(user -> !user.isDeleted())
                .ifPresent(user -> {

                    user.setStatus(false);
                    userRepository.save(user);
                });
    }

    public void unbanUser(Long userId) {

        userRepository.findById(userId)
                .filter(this::isCustomer)
                .filter(user -> !user.isDeleted())
                .ifPresent(user -> {

                    user.setStatus(true);
                    userRepository.save(user);
                });
    }

    public void softDeleteCustomer(Long userId) {

        userRepository.findById(userId)
                .filter(this::isCustomer)
                .filter(user -> !user.isDeleted())
                .ifPresent(user -> {

                    user.setDeleted(true);
                    user.setStatus(false);
                    userRepository.save(user);
                });
    }

    // ================= STAFF MANAGEMENT =================

    private StaffDTO toStaffDTO(User user) {
        return new StaffDTO(
                user.getId(),
                generateStaffCode(user.getId()),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getAddress(),
                user.isStatus(),
                user.isDeleted()
        );
    }

    private String generateStaffCode(Long id) {
        long value = Math.abs((id * 3571 + 93719) % 1000000);
        return String.format("STF-%06d", value);
    }

    private boolean isStaff(User u) {
        return u.getRole() != null && "STAFF".equalsIgnoreCase(u.getRole().getName());
    }

    public Page<StaffDTO> getStaffPage(String keyword, String status, Pageable pageable) {
        List<StaffDTO> staffList = userRepository.findAll()
                .stream()
                .filter(this::isStaff)
                .filter(u -> !u.isDeleted())
                .filter(u -> {
                    if (keyword == null || keyword.trim().isEmpty()) return true;
                    String key = keyword.toLowerCase();

                    return (u.getEmail() != null && u.getEmail().toLowerCase().contains(key))
                            || (u.getFullName() != null && u.getFullName().toLowerCase().contains(key))
                            || (u.getPhone() != null && u.getPhone().toLowerCase().contains(key))
                            || (u.getAddress() != null && u.getAddress().toLowerCase().contains(key));
                })
                .filter(u -> {
                    if (status == null || "all".equalsIgnoreCase(status)) return true;
                    if ("active".equalsIgnoreCase(status)) return u.isStatus();
                    if ("inactive".equalsIgnoreCase(status)) return !u.isStatus();
                    return true;
                })
                .map(this::toStaffDTO)
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), staffList.size());

        List<StaffDTO> pageContent = start >= staffList.size()
                ? List.of()
                : staffList.subList(start, end);

        return new PageImpl<>(pageContent, pageable, staffList.size());
    }



    public StaffDTO getStaffDTOById(Long id) {
        if (id == null) {
            return null;
        }
        return userRepository.findById(id)
                .filter(this::isStaff)
                .filter(user -> !user.isDeleted())
                .map(this::toStaffDTO)
                .orElse(null);
    }

    @Transactional
    public User createStaff(String fullName, String email, String password,
                            String phone, String address) {
        String normalizedFullName = validateAndNormalizeFullName(fullName);
        String normalizedEmail = normalizeEmail(email);
        String normalizedPhone = validateAndNormalizePhone(phone);
        String normalizedAddress = validateAndNormalizeAddress(address);
        validatePassword(password, "Password");

        if (normalizedEmail == null || normalizedEmail.isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException(
                    "This email address is already used by another customer or staff account."
            );
        }

        Role staffRole = getRoleByName(RoleName.STAFF);
        if (staffRole == null) {
            throw new IllegalStateException("The STAFF role is not configured.");
        }

        User staff = new User();
        staff.setFullName(normalizedFullName);
        staff.setEmail(normalizedEmail);
        staff.setPassword(passwordEncoder.encode(password));
        staff.setPhone(normalizedPhone);
        staff.setAddress(normalizedAddress);
        staff.setStatus(true);
        staff.setDeleted(false);
        staff.setAuthProvider(AuthProvider.LOCAL.name());
        staff.setRole(staffRole);

        return userRepository.save(staff);
    }

    @Transactional
    public void updateStaff(Long id, String fullName, String phone, String address) {
        User staff = findActiveStaffOrThrow(id);
        staff.setFullName(validateAndNormalizeFullName(fullName));
        staff.setPhone(validateAndNormalizePhone(phone));
        staff.setAddress(validateAndNormalizeAddress(address));
        userRepository.save(staff);
    }

    @Transactional
    public void updateStaffPassword(Long id, String newPassword) {
        validatePassword(newPassword, "New password");
        User staff = findActiveStaffOrThrow(id);
        staff.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(staff);
    }

    @Transactional
    public void banStaff(Long id) {
        User staff = findActiveStaffOrThrow(id);
        staff.setStatus(false);
        userRepository.save(staff);
    }

    @Transactional
    public void unbanStaff(Long id) {
        User staff = findActiveStaffOrThrow(id);
        staff.setStatus(true);
        userRepository.save(staff);
    }

    public long countStaff() {
        return userRepository.findAll().stream()
                .filter(this::isStaff)
                .filter(u -> !u.isDeleted())
                .count();
    }

    public long countActiveStaff() {
        return userRepository.findAll().stream()
                .filter(this::isStaff)
                .filter(u -> !u.isDeleted())
                .filter(User::isStatus)
                .count();
    }

    public long countInactiveStaff() {
        return userRepository.findAll().stream()
                .filter(this::isStaff)
                .filter(u -> !u.isDeleted())
                .filter(u -> !u.isStatus())
                .count();
    }

    public long countDeletedStaff() {
        return userRepository.findAll().stream()
                .filter(this::isStaff)
                .filter(User::isDeleted)
                .count();
    }

    private User findActiveStaffOrThrow(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Staff ID is required.");
        }

        return userRepository.findById(id)
                .filter(this::isStaff)
                .filter(user -> !user.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("Staff not found."));
    }

    private String validateAndNormalizeFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name is required.");
        }

        String normalized = fullName.trim().replaceAll("\\s+", " ");
        if (normalized.length() < 3 || normalized.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    "Full name must be between 3 and 150 characters."
            );
        }
        return normalized;
    }

    private String validateAndNormalizePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone number is required.");
        }

        String normalized = phone.trim();
        if (!normalized.matches(PHONE_PATTERN)) {
            throw new IllegalArgumentException(
                    "Please enter a valid Vietnamese phone number."
            );
        }
        return normalized;
    }

    private String validateAndNormalizeAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Address is required.");
        }

        String normalized = address.trim().replaceAll("\\s+", " ");
        if (normalized.length() > MAX_ADDRESS_LENGTH) {
            throw new IllegalArgumentException(
                    "Address must not exceed 500 characters."
            );
        }
        return normalized;
    }

    private void validatePassword(String password, String fieldName) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }

        if (!password.matches(PASSWORD_PATTERN)) {
            throw new IllegalArgumentException(
                    fieldName
                            + " must be between 8 and 72 characters and contain "
                            + "an uppercase letter, a lowercase letter, and a number."
            );
        }
    }
}