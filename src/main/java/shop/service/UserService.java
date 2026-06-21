package shop.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import shop.domain.dto.StaffDTO;
import shop.domain.Role;
import shop.domain.RoleName;
import shop.domain.User;
import shop.domain.dto.CustomerDTO;
import shop.domain.dto.RegisterDTO;
import shop.repository.RoleRepository;
import shop.repository.UserRepository;

@Service
public class UserService {

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
        user.setEmail(registerDTO.getEmail());
        user.setPassword(registerDTO.getPassword());
        return user;
    }

    public User handleSaveUser(User user) {
        return userRepository.save(user);
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    public Role getRoleByName(RoleName roleName) {
        return roleRepository.findByName(roleName.name());
    }

    public boolean checkEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void updatePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email);

        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }
    }

    // ================= CUSTOMER MANAGEMENT =================

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
        return String.format("CUS-%06d",
                Math.abs((id * 7919 + 104729) % 1000000));
    }

    public Page<CustomerDTO> getCustomersPage(
            String keyword,
            String status,
            Pageable pageable) {

        List<CustomerDTO> customers = userRepository.findAll()
                .stream()
                .filter(u -> u.getRole() != null
                        && "CUSTOMER".equalsIgnoreCase(u.getRole().getName()))
                .map(this::toCustomerDTO)
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), customers.size());

        List<CustomerDTO> pageContent =
                start >= customers.size()
                        ? List.of()
                        : customers.subList(start, end);

        return new PageImpl<>(pageContent, pageable, customers.size());
    }

    public CustomerDTO getCustomerDTOById(Long id) {
        return userRepository.findById(id)
                .map(this::toCustomerDTO)
                .orElse(null);
    }

    public long countCustomers() {
        return userRepository.findAll()
                .stream()
                .filter(u -> u.getRole() != null
                        && "CUSTOMER".equalsIgnoreCase(u.getRole().getName()))
                .count();
    }

    public long countActiveCustomers() {
        return userRepository.findAll()
                .stream()
                .filter(u -> u.getRole() != null
                        && "CUSTOMER".equalsIgnoreCase(u.getRole().getName()))
                .filter(User::isStatus)
                .count();
    }

    public long countInactiveCustomers() {
        return userRepository.findAll()
                .stream()
                .filter(u -> u.getRole() != null
                        && "CUSTOMER".equalsIgnoreCase(u.getRole().getName()))
                .filter(u -> !u.isStatus())
                .count();
    }

    public void banUser(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setStatus(false);
            userRepository.save(user);
        });
    }

    public void unbanUser(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setStatus(true);
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
                user.isDeleted(),
                user.getStaffRole()
        );
    }

    private String generateStaffCode(Long id) {
        long value = Math.abs((id * 3571 + 93719) % 1000000);
        return String.format("STF-%06d", value);
    }

    private boolean isStaff(User u) {
        return u.getRole() != null && "STAFF".equalsIgnoreCase(u.getRole().getName());
    }

    public Page<StaffDTO> getStaffPage(String keyword, String status, String staffRole, Pageable pageable) {
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
                .filter(u -> {
                    if (staffRole == null || "all".equalsIgnoreCase(staffRole)) return true;
                    return u.getStaffRole() != null && u.getStaffRole().equalsIgnoreCase(staffRole);
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

    public Page<StaffDTO> getDeletedStaffPage(String keyword, String staffRole, Pageable pageable) {
        List<StaffDTO> staffList = userRepository.findAll()
                .stream()
                .filter(this::isStaff)
                .filter(User::isDeleted)
                .filter(u -> {
                    if (keyword == null || keyword.trim().isEmpty()) return true;
                    String key = keyword.toLowerCase();

                    return (u.getEmail() != null && u.getEmail().toLowerCase().contains(key))
                            || (u.getFullName() != null && u.getFullName().toLowerCase().contains(key))
                            || (u.getPhone() != null && u.getPhone().toLowerCase().contains(key))
                            || (u.getAddress() != null && u.getAddress().toLowerCase().contains(key));
                })
                .filter(u -> {
                    if (staffRole == null || "all".equalsIgnoreCase(staffRole)) return true;
                    return u.getStaffRole() != null && u.getStaffRole().equalsIgnoreCase(staffRole);
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
        return userRepository.findById(id)
                .filter(this::isStaff)
                .map(this::toStaffDTO)
                .orElse(null);
    }

    public User createStaff(String fullName, String email, String password,
                            String phone, String address, String staffRole) {
        User staff = new User();
        staff.setFullName(fullName);
        staff.setEmail(email);
        staff.setPassword(passwordEncoder.encode(password));
        staff.setPhone(phone);
        staff.setAddress(address);
        staff.setStaffRole(staffRole);
        staff.setStatus(true);
        staff.setDeleted(false);
        staff.setRole(getRoleByName(RoleName.STAFF));

        return userRepository.save(staff);
    }

    public void updateStaff(Long id, String fullName, String phone, String address, String staffRole) {
        userRepository.findById(id).ifPresent(staff -> {
            staff.setFullName(fullName);
            staff.setPhone(phone);
            staff.setAddress(address);
            staff.setStaffRole(staffRole);
            userRepository.save(staff);
        });
    }

    public void updateStaffPassword(Long id, String newPassword) {
        userRepository.findById(id).ifPresent(staff -> {
            staff.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(staff);
        });
    }

    public void softDeleteStaff(Long id) {
        userRepository.findById(id).ifPresent(staff -> {
            staff.setDeleted(true);
            staff.setStatus(false);
            userRepository.save(staff);
        });
    }

    public void restoreStaff(Long id) {
        userRepository.findById(id).ifPresent(staff -> {
            staff.setDeleted(false);
            staff.setStatus(true);
            userRepository.save(staff);
        });
    }

    public void banStaff(Long id) {
        userRepository.findById(id).ifPresent(staff -> {
            staff.setStatus(false);
            userRepository.save(staff);
        });
    }

    public void unbanStaff(Long id) {
        userRepository.findById(id).ifPresent(staff -> {
            staff.setStatus(true);
            userRepository.save(staff);
        });
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
}