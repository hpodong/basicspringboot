package com.basicspringboot.providers;

import io.jsonwebtoken.*;
import jakarta.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtProvider {

    @Value("${jwt.secret.key}")
    private String memberBaseKey;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    private Key createKey() {
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(memberBaseKey);
        return new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
    }

    public String createAccessToken(long memberIdx, long expiredAt) {
        return createToken(expiredAt, memberIdx);
    }

    public String createRefreshToken(long expiredAt) {
        return createToken(expiredAt, null);
    }

    private String createToken(long expiredAt, Long idx) {
        Map<String, Object> headerMap = new HashMap<String, Object>();
        headerMap.put("typ", "JWT");
        headerMap.put("alg", "HS256");
        Map<String, Object> claims = new HashMap<String, Object>();
        if(idx != null) claims.put("idx", idx);
        Date expireTime = new Date();
        expireTime.setTime(expireTime.getTime() + expiredAt * 1000);

        JwtBuilder builder = Jwts.builder().setHeader(headerMap).setClaims(claims).setExpiration(expireTime).signWith(createKey(), signatureAlgorithm);

        return builder.compact();
    }

    public Long getIdx(String token) throws ExpiredJwtException{
        Long memberId = null;
        Claims claims = getTokenData(token);
        if(claims.get("idx") != null) memberId = ((Number) claims.get("idx")).longValue();
        return memberId;
    }

    public Claims getTokenData(String token) throws ExpiredJwtException {
        return Jwts.parserBuilder()
                .setSigningKey(DatatypeConverter.parseBase64Binary(memberBaseKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}