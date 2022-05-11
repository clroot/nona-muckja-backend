package io.nonamuckja.backend.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
	private final UserDetailsService userDetailsService;
	private final JwtTokenUtils jwtTokenUtils;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		final String requestTokenHeader = request.getHeader("Authorization");
		if (StringUtils.startsWith(requestTokenHeader, "Bearer ")) {
			String jwtToken = requestTokenHeader.substring(7);
			try {
				String username = jwtTokenUtils.getUsernameFromToken(jwtToken);
				if (StringUtils.isNotEmpty(username)) {
					UserDetails userDetails = userDetailsService.loadUserByUsername(username);
					if (jwtTokenUtils.validateToken(jwtToken, userDetails)) {
						UsernamePasswordAuthenticationToken authentication =
							new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

						authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
						SecurityContextHolder.getContext().setAuthentication(authentication);

					}
				}
			} catch (IllegalStateException e) {
				logger.error("Unable to fetch JWT Token");
			} catch (ExpiredJwtException e) {
				logger.error("JWT Token has expired");
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		} else {
			logger.warn("JWT Token does not begin with Bearer String");
		}
		filterChain.doFilter(request, response);
	}
}
