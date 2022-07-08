package flab.resellPlatform.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import flab.resellPlatform.common.ThreadLocalStandardResponseBucketHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

public class JWTUtils {

    private JWTUtils() {}

    static final int TOKEN_TYPE_INDEX = 1;
    static final int TOKEN_DATA_INDEX = 2;

    public static String getTokenData(String tokenRawData) throws ArrayIndexOutOfBoundsException {
        String[] jwtContents = tokenRawData.split(" ");
        return jwtContents[TOKEN_DATA_INDEX];
    }

    public static String getTokenType(String tokenRawData) throws ArrayIndexOutOfBoundsException {
        String[] jwtContents = tokenRawData.split(" ");
        return jwtContents[TOKEN_TYPE_INDEX];
    }

    public static void returnErrorCodeWithHeader(HttpServletResponse response, String message, HttpStatus badRequest) {
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "error=" + message);
        ThreadLocalStandardResponseBucketHolder.getResponse()
                .setHttpStatus(badRequest);
    }

    public static String getClaimWithVerificationProcess(String tokenData, String jwtSecretKey, String claimName) throws
            IllegalArgumentException,
            AlgorithmMismatchException,
            SignatureVerificationException,
            TokenExpiredException,
            InvalidClaimException {
        return JWT.require(Algorithm.HMAC512(jwtSecretKey)).build().verify(tokenData).getClaim(claimName).asString();
    }

    public static String createJwtToken(long id, String username, String subject, long expirationTime, Algorithm algorithm) {
        String jwtToken = JWT.create()
                .withSubject(subject)
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .withClaim("id", id)
                .withClaim("username", username)
                .sign(algorithm);
        return jwtToken;
    }

    public static DecodedJWT decodeJwtToken(String token) {
        return JWT.decode(token);
    }
}
