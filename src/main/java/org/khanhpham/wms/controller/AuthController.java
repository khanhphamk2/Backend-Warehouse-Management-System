package org.khanhpham.wms.controller;

import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.khanhpham.wms.domain.request.*;
import org.khanhpham.wms.domain.response.AuthResponse;
import org.khanhpham.wms.service.AuthService;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequiredArgsConstructor
@RequestMapping("${spring.data.rest.base-path}/auth")
public class AuthController {
    @Lazy
    private final AuthService authService;

    @Operation(summary = "Login user", description = "Authenticate user with username and password")
    @PostMapping(value = {"/login", "/sign-in"})
    public ResponseEntity<AuthResponse> login(
            @Parameter(description = "User login credentials") @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.loginWithIdentityAndPassword(request));
    }

    @Operation(summary = "Refresh token", description = "Refresh token with refresh token")
    @PostMapping(value = {"/refresh-token"})
    public ResponseEntity<AuthResponse> refresh(
            @Parameter(description = "Refresh token Request") @RequestBody RefreshTokenRequest request)
            throws ParseException, JOSEException {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @Operation(summary = "Register user", description = "Register user with username and password")
    @PostMapping(value = {"/register", "/sign-up"})
    public ResponseEntity<AuthResponse> register(
            @Parameter(description = "User register credentials") @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @Operation(summary = "Forgot password", description = "Send email to reset password")
    @PostMapping(value = {"/forgot-password"})
    public ResponseEntity<Void> forgotPassword(
            @Parameter(description = "User email to reset password")
            @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Reset password", description = "Reset password with reset password token")
    @PostMapping(value = {"/reset-password"})
    public ResponseEntity<Void> resetPassword(
            @Parameter(description = "Reset password token and new password")
            @RequestBody String resetPasswordToken,
            @RequestBody ResetPasswordRequest request) throws ParseException, JOSEException {
        authService.resetPassword(resetPasswordToken, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Logout", description = "Logout user")
    @PostMapping(value = {"/logout"})
    public ResponseEntity<Void> logout(
            @Parameter(description = "User logout credentials") @RequestBody LogoutRequest request)
            throws ParseException, JOSEException {
        authService.logout(request);
        return ResponseEntity.ok().build();
    }
}
