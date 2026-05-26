package shop.service.validator;

import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
import shop.service.UserService;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        shop.domain.User user = this.userService.getUserByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User Name Not Found");
        }

        return new User(
                user.getEmail(),
                user.getPassword(),
                user.isStatus(), // Maps to enabled (active) status
                true, // accountNonExpired
                true, // credentialsNonExpired
                user.isStatus(), // accountNonLocked, nếu tài khoản không bị khóa
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName())));

    }

}
