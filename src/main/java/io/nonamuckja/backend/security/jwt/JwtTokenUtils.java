package io.nonamuckja.backend.security.jwt;

import java.util.Date;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtTokenUtils {
	String generateToken(UserDetails userDetails);

	Boolean validateToken(String token, UserDetails userDetails);

	String getUsernameFromToken(String token);

	Date getExpirationDateFromToken(String token);
}
