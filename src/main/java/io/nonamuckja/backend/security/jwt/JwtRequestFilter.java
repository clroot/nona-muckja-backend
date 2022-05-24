package io.nonamuckja.backend.security.jwt;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.nonamuckja.backend.security.SecurityConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
	private static final String AUTHORIZATION_HEADER = SecurityConstants.AUTHORIZATION_HEADER;
	private static final String BEARER_PREFIX = SecurityConstants.BEARER_PREFIX;
	private final UserDetailsService userDetailsService;
	private final JwtTokenUtils jwtTokenUtils;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		final String jwtToken = resolveToken(request, response);

		if (jwtToken == null) {
			logger.warn("JWT Token does not begin with Bearer String");
			filterChain.doFilter(request, response);
			return;
		}

		try {
			String username = jwtTokenUtils.getUsernameFromToken(jwtToken);
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);

			if (jwtTokenUtils.validateToken(jwtToken, userDetails)) {
				UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (IllegalStateException e) {
			logger.error("Unable to fetch JWT Token");
		} catch (ExpiredJwtException e) {
			logger.error("JWT Token has expired");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request, HttpServletResponse response) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

		Optional<Cookie> optionalCookie = Optional.ofNullable(request.getCookies())
			.map(Arrays::asList)
			.orElse(Collections.emptyList())
			.stream()
			.filter(cookie -> StringUtils.equals(cookie.getName(), AUTHORIZATION_HEADER))
			.findAny();

		if (optionalCookie.isPresent() && StringUtils.isEmpty(bearerToken)) {
			try {
				bearerToken =
					BEARER_PREFIX + URLDecoder.decode(optionalCookie.get().getValue(), StandardCharsets.UTF_8);
			} catch (Exception e) {
				logger.error("Unable to decode JWT Token - invalid cookie");
				logger.error(e.getMessage());
				response.addCookie(new Cookie(AUTHORIZATION_HEADER, ""));
			}
		}
		if (StringUtils.isEmpty(bearerToken) || !bearerToken.startsWith(BEARER_PREFIX)) {
			return null;
		}

		return bearerToken.substring(BEARER_PREFIX.length());
	}
}
