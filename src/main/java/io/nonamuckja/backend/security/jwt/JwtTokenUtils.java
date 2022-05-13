package io.nonamuckja.backend.security.jwt;

import java.util.Date;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtTokenUtils {
	long JWT_TOKEN_VALIDITY = 60 * 24 * 7;

	String generateToken(UserDetails userDetails);

	Boolean validateToken(String token, UserDetails userDetails);

	String getUsernameFromToken(String token);

	Date getExpirationDateFromToken(String token);
}
