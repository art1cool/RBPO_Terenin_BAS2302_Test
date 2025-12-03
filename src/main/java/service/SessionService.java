package service;

import entity.UserEntity;
import entity.UserSessionEntity;
import enums.SessionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.UserSessionRepository;
import configuration.JwtTokenProvider;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final UserSessionRepository userSessionRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public UserSessionEntity createSession(UserEntity user, String refreshToken, String deviceInfo) {
        Optional<UserSessionEntity> existingSession = userSessionRepository.findByUserIdAndDeviceInfoAndStatus(
            user.getId(), deviceInfo, SessionStatus.ACTIVE);

        existingSession.ifPresent(session -> {
            session.setStatus(SessionStatus.LOGGED_OUT);
            userSessionRepository.save(session);
        });

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusSeconds(jwtTokenProvider.getRefreshExpiration() / 1000);

        UserSessionEntity session = UserSessionEntity.builder()
                .user(user)
                .refreshToken(refreshToken)
                .createdAt(now)
                .expiresAt(expiresAt)
                .status(SessionStatus.ACTIVE)
                .deviceInfo(deviceInfo)
                .build();

        return userSessionRepository.save(session);
    }

    @Transactional
    public UserSessionEntity handleRefreshOperation(UUID userId, String refreshToken) {
        if (jwtTokenProvider.isTokenExpired(refreshToken)) {
            Optional<UserSessionEntity> expiredSessionOpt = userSessionRepository.findByRefreshToken(refreshToken);
            expiredSessionOpt.ifPresent(session -> {
                if (session.getStatus() == SessionStatus.ACTIVE) {
                    session.setStatus(SessionStatus.EXPIRED);
                    userSessionRepository.save(session);
                }
            });
            return null;
        }

        Optional<UserSessionEntity> sessionOpt = userSessionRepository.findByRefreshToken(refreshToken);

        if (sessionOpt.isEmpty()) {
            return null;
        }

        UserSessionEntity session = sessionOpt.get();

        if (!session.getUser().getId().equals(userId)) {
            return null;
        }

        if (session.getStatus() == SessionStatus.LOGGED_OUT) {
            session.setStatus(SessionStatus.REVOKED);
            userSessionRepository.save(session);
            return null;
        }

        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            session.setStatus(SessionStatus.EXPIRED);
            userSessionRepository.save(session);
            return null;
        }

        if (session.getStatus() != SessionStatus.ACTIVE) {
            return null;
        }

        return session;
    }

    @Transactional
    public UserSessionEntity createNewSessionFromRefresh(UserSessionEntity oldSession, String newRefreshToken) {
        oldSession.setStatus(SessionStatus.LOGGED_OUT);
        userSessionRepository.save(oldSession);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusSeconds(jwtTokenProvider.getRefreshExpiration() / 1000);

        UserSessionEntity newSession = UserSessionEntity.builder()
                .user(oldSession.getUser())
                .refreshToken(newRefreshToken)
                .createdAt(now)
                .expiresAt(expiresAt)
                .status(SessionStatus.ACTIVE)
                .deviceInfo(oldSession.getDeviceInfo())
                .build();

        return userSessionRepository.save(newSession);
    }

    @Transactional
    public UserSessionEntity validateAndGetSession(UUID userId, String refreshToken) {
        Optional<UserSessionEntity> sessionOpt = userSessionRepository.findByRefreshTokenAndStatus(refreshToken, SessionStatus.ACTIVE);

        if (sessionOpt.isEmpty()) {
            return null;
        }

        UserSessionEntity session = sessionOpt.get();

        if (!session.getUser().getId().equals(userId)) {
            return null;
        }

        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            session.setStatus(SessionStatus.EXPIRED);
            userSessionRepository.save(session);
            return null;
        }

        return session;
    }

    @Transactional
    public void updateSession(UserSessionEntity session, String newRefreshToken) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime newExpiresAt = now.plusSeconds(jwtTokenProvider.getRefreshExpiration() / 1000);

        session.setRefreshToken(newRefreshToken);
        session.setExpiresAt(newExpiresAt);
        session.setStatus(SessionStatus.ACTIVE);

        userSessionRepository.save(session);
    }

    @Transactional
    public void revokeSession(UUID userId, String deviceInfo) {
        Optional<UserSessionEntity> sessionOpt = userSessionRepository.findByUserIdAndDeviceInfo(userId, deviceInfo);
        sessionOpt.ifPresent(session -> {
            session.setStatus(SessionStatus.REVOKED);
            userSessionRepository.save(session);
        });
    }

    @Transactional
    public void revokeAllSessions(UUID userId) {
        userSessionRepository.findByUserIdAndStatus(userId, SessionStatus.ACTIVE)
                .forEach(session -> {
                    session.setStatus(SessionStatus.REVOKED);
                    userSessionRepository.save(session);
                });
    }

    @Transactional
    public void cleanupExpiredSessions() {
        userSessionRepository.expireExpiredSessions(SessionStatus.EXPIRED, LocalDateTime.now());
        userSessionRepository.deleteExpiredSessions(LocalDateTime.now().minusDays(30));
    }
}