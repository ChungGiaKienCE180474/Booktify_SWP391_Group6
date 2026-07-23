package shop.service;

import java.util.Locale;
import java.util.Map;

import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;

@Service
public class UserSessionService {

    private final FindByIndexNameSessionRepository<? extends Session>
            sessionRepository;

    public UserSessionService(
            FindByIndexNameSessionRepository<? extends Session>
                    sessionRepository) {

        this.sessionRepository =
                sessionRepository;
    }

    /**
     * Xóa toàn bộ session đang hoạt động của một tài khoản.
     *
     * Dự án sử dụng Spring Session JDBC nên khi session bị xóa,
     * người dùng sẽ bị yêu cầu đăng nhập lại ở request tiếp theo.
     *
     * @param email email dùng để đăng nhập
     * @return số lượng session đã bị xóa
     */
    public int logoutUserImmediately(
            String email) {

        if (email == null
                || email.isBlank()) {

            return 0;
        }

        String normalizedEmail =
                email.trim()
                        .toLowerCase(
                                Locale.ROOT
                        );

        Map<String, ? extends Session> sessions =
                sessionRepository
                        .findByPrincipalName(
                                normalizedEmail
                        );

        if (sessions == null
                || sessions.isEmpty()) {

            return 0;
        }

        int deletedSessionCount = 0;

        for (String sessionId :
                sessions.keySet()) {

            if (sessionId == null
                    || sessionId.isBlank()) {

                continue;
            }

            sessionRepository.deleteById(
                    sessionId
            );

            deletedSessionCount++;
        }

        return deletedSessionCount;
    }

    /**
     * Kiểm tra một tài khoản hiện có session đăng nhập hay không.
     */
    public boolean hasActiveSession(
            String email) {

        if (email == null
                || email.isBlank()) {

            return false;
        }

        String normalizedEmail =
                email.trim()
                        .toLowerCase(
                                Locale.ROOT
                        );

        Map<String, ? extends Session> sessions =
                sessionRepository
                        .findByPrincipalName(
                                normalizedEmail
                        );

        return sessions != null
                && !sessions.isEmpty();
    }

    /**
     * Đếm số session đang hoạt động của một tài khoản.
     */
    public int countActiveSessions(
            String email) {

        if (email == null
                || email.isBlank()) {

            return 0;
        }

        String normalizedEmail =
                email.trim()
                        .toLowerCase(
                                Locale.ROOT
                        );

        Map<String, ? extends Session> sessions =
                sessionRepository
                        .findByPrincipalName(
                                normalizedEmail
                        );

        return sessions == null
                ? 0
                : sessions.size();
    }
}