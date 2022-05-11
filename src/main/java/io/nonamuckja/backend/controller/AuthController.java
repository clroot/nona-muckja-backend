package io.nonamuckja.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.nonamuckja.backend.dto.UserLoginFormDTO;
import io.nonamuckja.backend.security.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final UserDetailsService userDetailsService;
	private final JwtTokenUtils jwtTokenUtils;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody UserLoginFormDTO loginFormDTO) {
		log.info("login: {}", loginFormDTO);

		Map<String, Object> responseMap = new HashMap<>();

		final String username = loginFormDTO.getUsername();
		final String password = loginFormDTO.getPassword();

		try {
			Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(username, password)
			);
			if (auth.isAuthenticated()) {
				log.info("login success: {}", username);
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				String token = jwtTokenUtils.generateToken(userDetails);

				responseMap.put("token", token);
				responseMap.put("error", false);
				responseMap.put("message", "login success");

				return ResponseEntity.ok(responseMap);
			} else {
				log.info("login failed: {}", username);
				responseMap.put("error", true);
				responseMap.put("message", "Invalid Credentials");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap);
			}
		} catch (DisabledException e) {
			e.printStackTrace();
			responseMap.put("error", true);
			responseMap.put("message", "User is disabled");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
		} catch (BadCredentialsException e) {
			e.printStackTrace();
			responseMap.put("error", true);
			responseMap.put("message", "Invalid Credentials");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap);
		} catch (Exception e) {
			e.printStackTrace();
			responseMap.put("error", true);
			responseMap.put("message", "Unknown error");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
		}
	}
}
