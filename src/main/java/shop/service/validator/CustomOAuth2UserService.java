package shop.service.validator;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import shop.domain.User;
import shop.service.UserService;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    public CustomOAuth2UserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String email = oauth2User.getAttribute("email");
        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("invalid_user"), "Không lấy được email từ tài khoản Google.");
        }

        String fullName = oauth2User.getAttribute("name");
        String picture = oauth2User.getAttribute("picture");

        User user = userService.processOAuth2User(email, fullName, picture);

        if (!user.isStatus()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("account_locked"), "Tài khoản đã bị khóa.");
        }

        if (user.getRole() == null) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("invalid_user"), "Tài khoản chưa được gán vai trò.");
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName())),
                oauth2User.getAttributes(),
                "email");
    }
}
