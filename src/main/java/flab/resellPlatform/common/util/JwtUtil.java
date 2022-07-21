package flab.resellPlatform.common.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtil {

    public static String createToken(String subject, Date expiration, Map<String, Object> claims, SignatureAlgorithm algorithm, String secretKey) {
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(expiration)
                .setClaims(claims)
                .signWith(algorithm, secretKey)
                .compact();
    }

    public static Date generateExpDate(String timeout) {
        return new Date(System.currentTimeMillis() + Long.parseLong(timeout));
    }

}
