package org.khanhpham.wms.service;

import com.nimbusds.jose.JOSEException;
import org.khanhpham.wms.domain.request.*;
import org.khanhpham.wms.domain.response.AuthResponse;
import org.khanhpham.wms.domain.response.IntrospectResponse;

import java.text.ParseException;

public interface AuthService {
    IntrospectResponse introspect(IntrospectRequest request);
    void logout(LogoutRequest request) throws ParseException, JOSEException;
    AuthResponse loginWithIdentityAndPassword(LoginRequest loginRequest);
    AuthResponse refreshToken(RefreshTokenRequest request) throws ParseException, JOSEException;
    AuthResponse register(RegisterRequest registerRequest);
//    AuthResponse loginWithGoogle(HttpServletRequest request, LoginGoogleRequest loginGoogleRequest);
    void forgotPassword(ForgotPasswordRequest forgotPasswordRequest);
    void resetPassword(String resetPasswordToken, ResetPasswordRequest resetPasswordRequest) throws ParseException, JOSEException;
}
