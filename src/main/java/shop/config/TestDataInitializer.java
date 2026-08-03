package shop.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import shop.domain.User;
import shop.repository.RoleRepository;
import shop.repository.UserRepository;

@Component
public class TestDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public TestDataInitializer(UserRepository userRepository,
                               RoleRepository roleRepository,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Chỉ tạo nếu chưa có
        if (!userRepository.existsByEmail("admin@test.com")) {
            User admin = new User();
            admin.setEmail("admin@test.com");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setFullName("Admin Test");
            admin.setPhone("0123456789");
            admin.setAddress("HCM City");
            admin.setAvatar("");
            admin.setStatus(true);
            admin.setRole(roleRepository.findByName("ADMIN"));
            userRepository.save(admin);
            System.out.println(">>> ADMIN TEST ACCOUNT CREATED: admin@test.com / 123456");
        }
    }
}