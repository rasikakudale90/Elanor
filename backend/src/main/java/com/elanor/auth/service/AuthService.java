package com.elanor.auth.service;

import com.elanor.auth.dto.*;
import com.elanor.auth.entity.*;
import com.elanor.auth.repository.*;
import com.elanor.common.exception.BusinessException;
import com.elanor.common.exception.ErrorCode;
import com.elanor.common.provider.SmsProvider;
import com.elanor.common.provider.SocialAuthProvider;
import com.elanor.common.security.JwtTokenProvider;
import com.elanor.customer.entity.CustomerProfile;
import com.elanor.customer.repository.CustomerProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final OtpChallengeRepository otpChallengeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final SmsProvider smsProvider;
    private final SocialAuthProvider socialAuthProvider;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            CustomerProfileRepository customerProfileRepository,
            EmailVerificationTokenRepository emailVerificationTokenRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            OtpChallengeRepository otpChallengeRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            SmsProvider smsProvider,
            SocialAuthProvider socialAuthProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.customerProfileRepository = customerProfileRepository;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.otpChallengeRepository = otpChallengeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.smsProvider = smsProvider;
        this.socialAuthProvider = socialAuthProvider;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BusinessException(ErrorCode.CONFLICT, "An account with this email already exists.");
        }

        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_CUSTOMER", "Standard B2C Customer")));

        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setActive(true);
        user.setEmailVerified(false);
        user.getRoles().add(customerRole);
        user = userRepository.save(user);

        CustomerProfile profile = new CustomerProfile(user, request.getFirstName().trim(), request.getLastName(), request.getPhone());
        customerProfileRepository.save(profile);

        // Generate email verification token
        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = new EmailVerificationToken(
                user,
                token,
                Instant.now().plus(24, ChronoUnit.HOURS)
        );
        emailVerificationTokenRepository.save(verificationToken);
        log.info("[DEMO EMAIL NOTIFICATION] Email verification link for {}: /api/v1/auth/verify-email?token={}", normalizedEmail, token);

        List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), roles);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getId(),
                user.getEmail(),
                profile.getFirstName(),
                profile.getLastName(),
                roles
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_INVALID_CREDENTIALS));

        if (!user.isActive()) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Your account has been deactivated.");
        }

        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_CREDENTIALS);
        }

        CustomerProfile profile = customerProfileRepository.findByUser(user)
                .orElse(null);

        List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), roles);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getId(),
                user.getEmail(),
                profile != null ? profile.getFirstName() : "",
                profile != null ? profile.getLastName() : "",
                roles
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse refresh(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.AUTH_TOKEN_EXPIRED, "Invalid or expired refresh token.");
        }

        UUID userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User not found."));

        if (!user.isActive()) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Your account has been deactivated.");
        }

        CustomerProfile profile = customerProfileRepository.findByUser(user).orElse(null);
        List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), roles);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        return new AuthResponse(
                newAccessToken,
                newRefreshToken,
                user.getId(),
                user.getEmail(),
                profile != null ? profile.getFirstName() : "",
                profile != null ? profile.getLastName() : "",
                roles
        );
    }

    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Invalid verification token."));

        if (verificationToken.isUsed() || verificationToken.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException(ErrorCode.AUTH_TOKEN_EXPIRED, "Verification token has expired or has already been used.");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        verificationToken.setUsed(true);
        emailVerificationTokenRepository.save(verificationToken);
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        Optional<User> userOpt = userRepository.findByEmail(normalizedEmail);
        if (userOpt.isEmpty()) {
            // Return quietly to avoid user enumeration
            return;
        }

        User user = userOpt.get();
        emailVerificationTokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(user, token, Instant.now().plus(1, ChronoUnit.HOURS));
        passwordResetTokenRepository.save(resetToken);

        log.info("[DEMO EMAIL NOTIFICATION] Password reset link for {}: /reset-password?token={}", normalizedEmail, token);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Invalid password reset token."));

        if (resetToken.isUsed() || resetToken.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException(ErrorCode.AUTH_TOKEN_EXPIRED, "Password reset token has expired or has already been used.");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    @Transactional
    public void requestOtp(RequestOtpRequest request) {
        String phone = request.getPhone().trim();
        String otp = String.format("%06d", secureRandom.nextInt(1000000));
        String otpHash = passwordEncoder.encode(otp);

        OtpChallenge challenge = new OtpChallenge(phone, otpHash, Instant.now().plus(5, ChronoUnit.MINUTES));
        otpChallengeRepository.save(challenge);

        smsProvider.sendOtp(phone, otp);
    }

    @Transactional
    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        String phone = request.getPhone().trim();
        OtpChallenge challenge = otpChallengeRepository.findTopByPhoneAndVerifiedFalseOrderByCreatedAtDesc(phone)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_OTP_INVALID, "No active OTP request found for this phone number."));

        if (challenge.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException(ErrorCode.AUTH_OTP_EXPIRED);
        }

        if (challenge.getAttemptCount() >= 5) {
            throw new BusinessException(ErrorCode.RATE_LIMIT_EXCEEDED, "Too many failed OTP verification attempts.");
        }

        challenge.setAttemptCount(challenge.getAttemptCount() + 1);

        if (!passwordEncoder.matches(request.getOtp().trim(), challenge.getOtpHash())) {
            otpChallengeRepository.save(challenge);
            throw new BusinessException(ErrorCode.AUTH_OTP_INVALID);
        }

        challenge.setVerified(true);
        otpChallengeRepository.save(challenge);

        // Find or create customer
        User user = userRepository.findByPhone(phone).orElseGet(() -> {
            Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_CUSTOMER", "Standard B2C Customer")));

            User newUser = new User();
            newUser.setEmail(phone + "@phone.elanor.internal");
            newUser.setPhone(phone);
            newUser.setActive(true);
            newUser.setEmailVerified(true);
            newUser.getRoles().add(customerRole);
            newUser = userRepository.save(newUser);

            CustomerProfile newProfile = new CustomerProfile(newUser, "Customer", "", phone);
            customerProfileRepository.save(newProfile);
            return newUser;
        });

        CustomerProfile profile = customerProfileRepository.findByUser(user).orElse(null);
        List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), roles);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getId(),
                user.getEmail(),
                profile != null ? profile.getFirstName() : "",
                profile != null ? profile.getLastName() : "",
                roles
        );
    }

    @Transactional
    public AuthResponse loginWithGoogle(GoogleLoginRequest request) {
        SocialAuthProvider.SocialUserProfile googleUser = socialAuthProvider.verifyToken(request.getIdToken());
        String normalizedEmail = googleUser.getEmail().toLowerCase().trim();

        User user = userRepository.findByEmail(normalizedEmail).orElseGet(() -> {
            Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_CUSTOMER", "Standard B2C Customer")));

            User newUser = new User();
            newUser.setEmail(normalizedEmail);
            newUser.setActive(true);
            newUser.setEmailVerified(true);
            newUser.getRoles().add(customerRole);
            newUser = userRepository.save(newUser);

            CustomerProfile newProfile = new CustomerProfile(newUser, googleUser.getFirstName(), googleUser.getLastName(), "");
            newProfile.setAvatarUrl(googleUser.getAvatarUrl());
            customerProfileRepository.save(newProfile);
            return newUser;
        });

        CustomerProfile profile = customerProfileRepository.findByUser(user).orElse(null);
        List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), roles);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getId(),
                user.getEmail(),
                profile != null ? profile.getFirstName() : "",
                profile != null ? profile.getLastName() : "",
                roles
        );
    }
}
