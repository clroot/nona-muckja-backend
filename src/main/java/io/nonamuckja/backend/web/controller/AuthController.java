package io.nonamuckja.backend.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.nonamuckja.backend.domain.user.User;
import io.nonamuckja.backend.domain.user.UserRepository;
import io.nonamuckja.backend.service.AuthService;
import io.nonamuckja.backend.web.dto.TokenResponseDTO;
import io.nonamuckja.backend.web.dto.UserDTO;
import io.nonamuckja.backend.web.dto.UserLoginFormDTO;
import io.nonamuckja.backend.web.dto.UserRegisterFormDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
	private final AuthService authService;
	private final UserRepository userRepository;

	@PostMapping("/login")
	public ResponseEntity<TokenResponseDTO> login(@RequestBody UserLoginFormDTO loginFormDTO) {
		log.info("login: {}", loginFormDTO.getUsername());
		TokenResponseDTO token = authService.login(loginFormDTO);
		log.info("login success: {}", loginFormDTO.getUsername());
		return ResponseEntity.ok(token);
	}

	@PostMapping("/register")
	public ResponseEntity<UserDTO> register(@RequestBody UserRegisterFormDTO userDTO) {
		log.info("User registration request received: {}", userDTO);

		Long registeredId = authService.register(userDTO);
		User user = userRepository.findById(registeredId).orElseThrow();
		log.info("User registration successful: {}", user);

		return new ResponseEntity<>(UserDTO.fromEntity(user), HttpStatus.CREATED);
	}
}
