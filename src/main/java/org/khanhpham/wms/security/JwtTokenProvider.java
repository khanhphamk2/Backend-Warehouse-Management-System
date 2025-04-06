package org.khanhpham.wms.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.khanhpham.wms.exception.ResourceNotFoundException;
import org.khanhpham.wms.repository.InvalidatedTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${app.jwt.signer-key}")
    private String signerKey;

    @NonFinal
    @Value("${app.jwt.valid-duration}")
    private long validDuration;

    @Value("${app.jwt.refreshable-duration}")
    private long refreshableDuration;

    @Value("${app.jwt.reset-password-duration}")
    private long resetPasswordDuration;

    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, validDuration);
    }

    public String generateRefreshToken(Authentication authentication) {
        return generateToken(authentication, refreshableDuration);
    }

    public String generateResetPasswordToken(Authentication authentication) {
        return generateToken(authentication, resetPasswordDuration);
    }

    private String generateToken(Authentication auth, long duration) {
        UserDetails userPrincipal = (UserDetails) auth.getPrincipal();
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = buildClaims(userPrincipal, duration);
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new JwtException("Failed to generate token", e);
        }
    }

    private JWTClaimsSet buildClaims(UserDetails user, long duration) {
        return new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("wms.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(duration, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();
    }

    public SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        log.info("Verifying token: {}", token);
        JWSVerifier verifier = new MACVerifier(signerKey.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT
                .getJWTClaimsSet()
                .getIssueTime()
                .toInstant()
                .plus(refreshableDuration, ChronoUnit.SECONDS)
                .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        log.info("Token verification result: {}", verified);
        log.info("Token expiry time: {}", expiryTime);

        if (!verified) throw new JwtException("Invalid token signature");
        if (!expiryTime.after(new Date())) throw new JwtException("Token expired");

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            log.warn("Token verification failed: {}", token);
            throw new ResourceNotFoundException("Token", "id", signedJWT.getJWTClaimsSet().getJWTID());
        }

        return signedJWT;
    }

    public long getExpirationTime(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getExpirationTime().getTime();
        } catch (ParseException e) {
            throw new JwtException("Invalid token", e);
        }
    }

    private String buildScope(@NotNull UserDetails user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getAuthorities()))
            user.getAuthorities().forEach(role ->
                    stringJoiner.add("ROLE_" + role));

        return stringJoiner.toString();
    }
}
