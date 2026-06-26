package shop.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import shop.domain.User;
import shop.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    public CustomSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    protected String determineTargetUrl(final Authentication authentication) {
        User user = this.userService.getUserByEmail(authentication.getName());

        if (user != null && user.getRole() != null && "ADMIN".equalsIgnoreCase(user.getRole().getName())) {
            return "/admin";
        }

        if (user != null && user.getRole() != null && "STAFF".equalsIgnoreCase(user.getRole().getName())) {
            return "/staff";
        }

        return "/";
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, Authentication authentication) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }

        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

        String email = authentication.getName();
        User user = this.userService.getUserByEmail(email);

        if (user != null) {
            session.setAttribute("username", user.getEmail());
            session.setAttribute("fullName", user.getFullName());
            session.setAttribute("avatar", user.getAvatar());
            session.setAttribute("id", user.getId());
            session.setAttribute("email", user.getEmail());

            if (user.getRole() != null) {
                session.setAttribute("role", user.getRole().getName());
            }
        }
    }

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        String email = authentication.getName();

        User user = this.userService.getUserByEmail(email);

        if (user != null && !user.isStatus()) {
            response.sendRedirect("/login?locked");
            return;
        }

        String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            return;
        }

        clearAuthenticationAttributes(request, authentication);

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }
}