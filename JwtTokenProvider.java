package com.banking.jbs.config;

import java.security.Key;

//import java.sql.Date;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
//import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

//import lombok.Value;

@Component
public class JwtTokenProvider {

	
	@Value("${app.jwt-secret}")
	private String jwtSecret;
	
	@Value("${app.jwt-expiration}")
	private long jwtExpirationDate;
	
	public String generateToken(Authentication authentication) {
		String username = authentication.getName();
		Date currentDate = new Date();
		Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);
		
		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(currentDate)
				.setExpiration(expireDate)
				.signWith(key())
				.compact();
				
	}
	
	private Key key() {
		byte[] bytes = Decoders.BASE64.decode(jwtSecret);
		return Keys.hmacShaKeyFor(bytes);
	}
	
	public String getUsername(String token) {
		Claims claims = Jwts.parser()
				            .setSigningKey(key())
				            .build()
				            .parseClaimsJws(token)
				            .getBody();
		return claims.getSubject();
	}
	
	public boolean valiadateToken(String token) {
		try {
			Jwts.parser()
			.setSigningKey(key())
			.build()
			.parse(token);
			
			return true;
		} catch (ExpiredJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
			throw new RuntimeException();
		}
	}
}
