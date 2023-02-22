package com.devsss.aoeba.login.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
public class JwtUtil {

    private static JwtDecoder jwtDecoder;

    private static JwtEncoder jwtEncoder;

    public static void setJwtDecoder(JwtDecoder jwtDecoder) {
        log.debug("设置JwtUtil.jwtDecoder");
        JwtUtil.jwtDecoder = jwtDecoder;
    }

    public static void setJwtEncoder(JwtEncoder jwtEncoder) {
        log.debug("设置JwtUtil.jwtEncoder");
        JwtUtil.jwtEncoder = jwtEncoder;
    }

    public static String getToken(String username, Collection<? extends GrantedAuthority> authorities) {
        Instant now = Instant.now();
        long expiry = 36000L;

        String scope = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(username)
                .claim("scope", scope)
                .build();
        String tokenValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        log.debug("生成token:" + tokenValue);
        return tokenValue;
    }
}
