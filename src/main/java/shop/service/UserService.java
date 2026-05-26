package shop.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import shop.domain.Role;
import shop.domain.RoleName;
import shop.domain.User;
import shop.domain.dto.RegisterDTO;
import shop.repository.RoleRepository;
import shop.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
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
}
