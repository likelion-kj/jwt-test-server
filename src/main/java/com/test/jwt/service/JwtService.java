package com.test.jwt.service;

import com.test.jwt.entity.Member;
import io.jsonwebtoken.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.Duration;
import java.util.Date;

@Service
public class JwtService {
    // TODO secretKey 누출되지 않도록, 추후 secret한 값으로 변경
    private final static String SECRET_KEY = "secretKey";

    public String makeJwt(Member member){
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + Duration.ofMinutes(60).toMillis()))
                .claim("id", member.getUserId())
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public void validateJwt(String userId, String token){
        Claims claims;

        try {
            claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException();
        } catch (Exception e) {
            throw new InvalidTokenException();
        }

        if(!userId.equals(claims.get("id"))) {
            throw new NoMachUseridException();
        }
    }

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Token is expired!")
    public static class ExpiredTokenException extends RuntimeException {}

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Token is invalid!")
    public static class InvalidTokenException extends RuntimeException {}

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class NoMachUseridException extends RuntimeException {}
}
