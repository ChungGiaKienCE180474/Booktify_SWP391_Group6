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

        // Get the email from the authentication object
        String email = authentication.getName();

        // Query the user based on the email
        User user = this.userService.getUserByEmail(email);

        // Redirect to /login?locked if the account is disabled
        if (!user.isStatus()) {
            // Redirect to login with a parameter indicating the account is locked
            response.sendRedirect("/login?locked");
            return;
        }
        // If the account is active, determine the target URL based on the role
        String targetUrl = determineTargetUrl(authentication);

        // Check if the response has already been committed
        if (response.isCommitted()) {
            return;
        }

        // Redirect to the target URL
        redirectStrategy.sendRedirect(request, response, targetUrl);

        // Clear authentication attributes (e.g., exceptions) and set user details in
        // the session
        clearAuthenticationAttributes(request, authentication);

    }

}
