package com.test.jwt.service


import io.jsonwebtoken.Header
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import spock.lang.Specification

import java.time.Duration

class JwtServiceUnitTest extends Specification {
    def "ValidateJwt. token is expired"() {
        JwtService service = new JwtService()

        given:
        String userId = "testId1"
        String token = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() - Duration.ofMinutes(60).toMillis()))
                .claim("id", userId)
                .signWith(SignatureAlgorithm.HS256, "secretKey")
                .compact();

        when:
        service.validateJwt(userId, token)

        then:
        thrown(JwtService.ExpiredTokenException)
    }

    def "ValidateJwt. signature is invalid"() {
        JwtService service = new JwtService()

        given:
        String userId = "testId1"
        String token = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + Duration.ofMinutes(60).toMillis()))
                .claim("id", userId)
                .signWith(SignatureAlgorithm.HS256, "invalidKey")
                .compact();

        when:
        service.validateJwt(userId, token)

        then:
        thrown(JwtService.InvalidTokenException)
    }

    def "ValidateJwt. invalid user"() {
        JwtService service = new JwtService()

        given:
        String userId = "invalidId"
        String token = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + Duration.ofMinutes(60).toMillis()))
                .claim("id", "testId1")
                .signWith(SignatureAlgorithm.HS256, "secretKey")
                .compact();

        when:
        service.validateJwt(userId, token)

        then:
        thrown(JwtService.NoMachUseridException)
    }

    def "ValidateJwt. success"() {
        JwtService service = new JwtService()

        given:
        String userId = "testId1"
        String token = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + Duration.ofMinutes(60).toMillis()))
                .claim("id", userId)
                .signWith(SignatureAlgorithm.HS256, "secretKey")
                .compact();

        when:
        service.validateJwt(userId, token)

        then:
        notThrown()
    }
}
