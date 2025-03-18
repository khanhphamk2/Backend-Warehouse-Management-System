package org.khanhpham.wms.service.impl;

import com.nimbusds.jose.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.khanhpham.wms.domain.dto.UserDTO;
import org.khanhpham.wms.domain.model.InvalidatedToken;
import org.khanhpham.wms.domain.model.User;
import org.khanhpham.wms.domain.request.*;
import org.khanhpham.wms.domain.response.AuthResponse;
import org.khanhpham.wms.domain.response.IntrospectResponse;
import org.khanhpham.wms.exception.CustomException;
import org.khanhpham.wms.repository.InvalidatedTokenRepository;
import org.khanhpham.wms.security.JwtTokenProvider;
import org.khanhpham.wms.service.AuthService;
import org.khanhpham.wms.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.Instant;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final InvalidatedTokenRepository invalidatedTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    @Setter
    private AuthService authService;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$");

    @Value("${app.frontend}")
    private String frontEndUrl;

    @Override
    @Transactional(readOnly = true)
    public AuthResponse loginWithIdentityAndPassword(LoginRequest loginRequest) {
        String identity = loginRequest.getIdentity();
        if (!userService.existsByUsernameOrEmail(identity)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "Username or password is incorrect!");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            identity, loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate tokens
            var accessToken = jwtTokenProvider.generateAccessToken(authentication);
            var refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
            var expire = jwtTokenProvider.getExpirationTime(refreshToken);

            // Get user details after successful authentication
            UserDTO user = userService.findByUsernameOrEmail(identity, identity);

            return AuthResponse.builder()
                    .user(user)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .expiresAt(expire)
                    .authenticated(true)
                    .build();

        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}", identity, e);
            throw new CustomException(HttpStatus.UNAUTHORIZED, "Username or password is incorrect!");
        }
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) throws ParseException, JOSEException {
        var signedJWT = jwtTokenProvider.verifyToken(request.getRefreshToken(), true);

        if (signedJWT == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        var jit = signedJWT.getJWTClaimsSet().getJWTID();

        if (invalidatedTokenRepository.existsById(jit)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "Token has been revoked");
        }

        var expiryTime = signedJWT.getJWTClaimsSet()
                .getExpirationTime()
                .toInstant();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);

        var username = signedJWT.getJWTClaimsSet().getSubject();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        var accessToken = jwtTokenProvider.generateAccessToken(authentication);
        var refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        var expire = jwtTokenProvider.getExpirationTime(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresAt(expire)
                .authenticated(true)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public IntrospectResponse introspect(IntrospectRequest request) {
        var token = request.getToken();
        try {
            var signToken = jwtTokenProvider.verifyToken(token, false);
            if (signToken == null) {
                return IntrospectResponse.builder().valid(false).build();
            }

            // Check if token is invalidated
            String jit = signToken.getJWTClaimsSet().getJWTID();
            if (invalidatedTokenRepository.existsById(jit)) {
                return IntrospectResponse.builder().valid(false).build();
            }

            return IntrospectResponse.builder().valid(true).build();
        } catch (Exception e) {
            return IntrospectResponse.builder().valid(false).build();
        }
    }

    @Override
    @Transactional
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = jwtTokenProvider.verifyToken(request.getToken(), true);
            if (signToken == null) {
                return; // Token already invalid
            }

            String jit = signToken.getJWTClaimsSet().getJWTID();
            if (invalidatedTokenRepository.existsById(jit)) {
                return;
            }

            Instant expiryTime = signToken.getJWTClaimsSet().getExpirationTime().toInstant();

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jit)
                    .expiryTime(expiryTime)
                    .build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (Exception exception) {
            log.info("Token already expired or invalid");
            throw new CustomException(HttpStatus.UNAUTHORIZED, "Token already expired or invalid");
        }
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        String password = registerRequest.getPassword();

        if (isPasswordValid(password)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Invalid password format.");
        }

        if (userService.existsByUsername(registerRequest.getUsername())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        if (userService.existsByEmail(registerRequest.getEmail())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        String encodedPassword = passwordEncoder.encode(password);
        UserDTO user = userService.createUser(registerRequest, encodedPassword);

        if (user == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "User cannot be created");
        }

        return authService.loginWithIdentityAndPassword(
                new LoginRequest(registerRequest.getUsername(), registerRequest.getPassword()));
    }

    @Override
    @Transactional(readOnly = true)
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        try {
            String email = forgotPasswordRequest.getEmail();
            Optional<User> user = userService.findByEmail(email);
            if (user.isEmpty()) {
                throw new CustomException(HttpStatus.NOT_FOUND, "User not found");
            }
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.get().getUsername());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateResetPasswordToken(authentication);

            String subject = "Reset your password";
            String content = "Please click the link below to reset your password: \n"
                    + frontEndUrl + "/reset-password/" + "?tokenValue=" + token;

            // Send email implementation should be here
            // emailService.sendEmail(email, subject, content);
        } catch (Exception e) {
            log.error("Error in forgotPassword: ", e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Error sending email");
        }
    }

    @Override
    @Transactional
    public void resetPassword(String resetPasswordToken, ResetPasswordRequest resetPasswordRequest) throws ParseException, JOSEException {
        String password = resetPasswordRequest.getPassword();
        if (isPasswordValid(password)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Invalid password format.");
        }

        var signedJWT = jwtTokenProvider.verifyToken(resetPasswordToken, false);
        if (signedJWT == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "Invalid reset token");
        }

        String jit = signedJWT.getJWTClaimsSet().getJWTID();
        if (invalidatedTokenRepository.existsById(jit)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "Token has already been used");
        }

        Instant expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime().toInstant();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);

        var username = signedJWT.getJWTClaimsSet().getSubject();
        userService.changePassword(username, passwordEncoder.encode(password));
    }

    private boolean isPasswordValid(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}
