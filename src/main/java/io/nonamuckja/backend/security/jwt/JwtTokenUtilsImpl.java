package io.nonamuckja.backend.security.jwt;

import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.nonamuckja.backend.security.SecurityConstants;

@Component
public class JwtTokenUtilsImpl implements JwtTokenUtils, Serializable {

	private static final String AUTHORIZATION_HEADER = SecurityConstants.AUTHORIZATION_HEADER;
	private static final String BEARER_PREFIX = SecurityConstants.BEARER_PREFIX;

	private final Key key;

	public JwtTokenUtilsImpl(@Value("${nonamuckja.jwt-secret}") String secretString) {
		this.key = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public Cookie generateAccessTokenCookie(String token) {
		String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);

		Cookie accessTokenCookie = new Cookie(AUTHORIZATION_HEADER, encodedToken);
		accessTokenCookie.setPath("/");
		accessTokenCookie.setHttpOnly(true);
		accessTokenCookie.setMaxAge((int)(getExpirationDateFromToken(token).getTime() - new Date().getTime()));

		return accessTokenCookie;
	}

	@Override
	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		return Jwts.builder()
			.setClaims(claims)
			.setSubject(userDetails.getUsername())
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
			.signWith(key)
			.compact();
	}

	@Override
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	@Override
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	@Override
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
	}

	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}
}
