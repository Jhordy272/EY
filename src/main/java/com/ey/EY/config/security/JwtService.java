package com.ey.EY.config.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    public String generateToken(String username) throws JOSEException {
        JWSSigner signer = new MACSigner(secret.getBytes(StandardCharsets.UTF_8));

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(username)
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plusMillis(expirationMs)))
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    public String extractUsername(String token) throws Exception {
        return parseAndVerify(token).getJWTClaimsSet().getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            SignedJWT jwt = parseAndVerify(token);
            Date expiry = jwt.getJWTClaimsSet().getExpirationTime();
            return expiry != null && expiry.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private SignedJWT parseAndVerify(String token) throws Exception {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(secret.getBytes(StandardCharsets.UTF_8));
        if (!signedJWT.verify(verifier)) {
            throw new SecurityException("Firma JWT inválida");
        }
        return signedJWT;
    }
}
