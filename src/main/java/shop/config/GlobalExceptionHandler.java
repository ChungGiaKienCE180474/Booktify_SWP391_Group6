package shop.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Lưới an toàn cuối cho toàn bộ MVC controller: bắt các exception chưa được
 * controller xử lý để KHÔNG lộ trang Whitelabel + stacktrace ra người dùng.
 * Log đầy đủ phía server, hiển thị trang lỗi thân thiện kèm mã tham chiếu.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * KHÔNG tự xử lý 403 — ném lại để Spring Security điều hướng tới trang
     * /access-deny (accessDeniedPage đã cấu hình). Handler cụ thể này thắng
     * handler Exception chung ở dưới nên authz vẫn hoạt động đúng.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDenied(AccessDeniedException ex) throws AccessDeniedException {
        throw ex;
    }

    /** File upload vượt giới hạn (5MB) → quay lại trang trước kèm thông báo. */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleUploadTooLarge(
            MaxUploadSizeExceededException ex,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute(
                "errorMessage",
                "Uploaded file is too large. The maximum allowed size is 5MB."
        );
        return "redirect:" + safeReferer(request);
    }

    /**
     * Chỉ chấp nhận Referer cùng host và trả về PATH tương đối để tránh
     * open-redirect (kẻ tấn công không thể đẩy nạn nhân sang domain ngoài).
     */
    private String safeReferer(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer == null || referer.isBlank()) {
            return "/";
        }
        try {
            java.net.URI uri = java.net.URI.create(referer);
            String host = uri.getHost();
            if (host == null || host.equalsIgnoreCase(request.getServerName())) {
                String path = uri.getPath();
                return (path != null && path.startsWith("/")) ? path : "/";
            }
        } catch (Exception ignored) {
            // Referer không hợp lệ → về trang chủ
        }
        return "/";
    }

    /** Mọi lỗi chưa xử lý khác → log + trang lỗi thân thiện (HTTP 500). */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleUnexpected(Exception ex, HttpServletRequest request) {
        String errorRef = "ERR-" + System.currentTimeMillis();
        log.error("Unhandled exception [{}] on {} {}",
                errorRef, request.getMethod(), request.getRequestURI(), ex);

        ModelAndView mav = new ModelAndView("error/500");
        mav.addObject("errorRef", errorRef);
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return mav;
    }
}
