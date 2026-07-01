package shop.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;
import org.springframework.context.annotation.Lazy;
import shop.domain.User;
import shop.service.UserService;
import shop.service.validator.CustomOAuth2UserService;
import shop.service.validator.CustomUserDetailsService;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    @Lazy
    private CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return new CustomUserDetailsService(userService);
    }

    @Bean
    public DaoAuthenticationProvider authProvider(
            PasswordEncoder passwordEncoder,
            UserDetailsService userDetailsService) {

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        authProvider.setHideUserNotFoundExceptions(false);

        return authProvider;
    }

    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return new CustomSuccessHandler(userService);
    }

    @Bean
    public SpringSessionRememberMeServices rememberMeServices() {
        SpringSessionRememberMeServices rememberMeServices = new SpringSessionRememberMeServices();
        // optionally customize
        rememberMeServices.setAlwaysRemember(true);
        return rememberMeServices;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, DaoAuthenticationProvider authProvider) throws Exception {

        http
                .authenticationProvider(authProvider)
                .authorizeHttpRequests(authorize -> authorize
                .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.INCLUDE)
                .permitAll()
                .requestMatchers("/", "/login", "/register", "/css/**",
                        "/js/**", "/images/**", "/forgotpassword",
                        "/authentication/**", "/books", "/books/**", "/client/**",
                        "/logout", "/logout/**")
                .permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/changepass", "/profile", "/profile/**", "/cart", "/cart/**").authenticated()
                .requestMatchers("/stationery/**").authenticated()
                .anyRequest().authenticated())
                .csrf(csrf -> csrf
                .ignoringRequestMatchers("/authentication/**", "/register"))
                .sessionManagement(sessionManagement -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .invalidSessionUrl("/login?expired")
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false))
                .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .permitAll())
                .rememberMe(r -> r
                .rememberMeServices(rememberMeServices()))
                .formLogin(formLogin -> formLogin
                .loginPage("/login")
                .failureHandler(this::handleLoginFailure) // Gọi phương thức xử lý lỗi
                .successHandler(customSuccessHandler())
                .permitAll())
                .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(customOAuth2UserService))
                .successHandler(customSuccessHandler())
                .failureHandler(this::handleOAuth2LoginFailure))
                .exceptionHandling(ex -> ex
                .accessDeniedPage("/access-deny"));
                

        return http.build();
    }

    // Phương thức xử lý lỗi đăng nhập
    private void handleLoginFailure(HttpServletRequest request, HttpServletResponse response,
            org.springframework.security.core.AuthenticationException exception) throws IOException {
        String failureMessage = exception != null ? exception.getMessage() : null;
        String email = request.getParameter("username");
        User user = userService.getUserByEmail(email);

        if (user != null) {
            if (!user.isStatus()) {
                // Hiện thông báo tài khoản bị cấm
                request.getSession().setAttribute("message", "Tài khoản của bạn đã bị cấm.");
                response.sendRedirect("/login?locked"); // Chuyển hướng tới trang đăng nhập
            } else {
                response.sendRedirect("/login?error"); // Chuyển hướng cho các lỗi khác
            }
        } else {
            if (failureMessage != null && !failureMessage.isBlank()) {
                request.getSession().setAttribute("message", failureMessage);
            }
            response.sendRedirect("/login?error"); // Người dùng không tồn tại
        }
    }

    private void handleOAuth2LoginFailure(HttpServletRequest request, HttpServletResponse response,
            org.springframework.security.core.AuthenticationException exception) throws IOException {
        if (exception instanceof OAuth2AuthenticationException oauth2Exception
                && "account_locked".equals(oauth2Exception.getError().getErrorCode())) {
            response.sendRedirect("/login?locked");
            return;
        }
        request.getSession().setAttribute("message", "Đăng nhập Google thất bại. Vui lòng thử lại.");
        response.sendRedirect("/login?error");
    }
}
