package shop.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import shop.domain.RoleName;
import shop.domain.User;
import shop.repository.UserRepository;
import shop.service.UserService;

@Component
@Order(2)
public class UserDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserDataInitializer(UserRepository userRepository, UserService userService,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        List<UserSeed> seeds = List.of(
                new UserSeed("admin@booktify.local", "123456", "Booktify Admin", RoleName.ADMIN),
                new UserSeed("staff@booktify.local", "Staff@123", "Booktify Staff", RoleName.STAFF),
                new UserSeed("customer@booktify.local", "Customer@123", "Booktify Customer", RoleName.CUSTOMER));

        for (UserSeed seed : seeds) {
            if (userRepository.existsByEmail(seed.email())) {
                continue;
            }

            User user = new User();
            user.setEmail(seed.email());
            user.setPassword(passwordEncoder.encode(seed.password()));
            user.setFullName(seed.fullName());
            user.setStatus(true);
            user.setRole(userService.getRoleByName(seed.roleName()));
            userRepository.save(user);
        }
    }

    private record UserSeed(String email, String password, String fullName, RoleName roleName) {

    }
}
