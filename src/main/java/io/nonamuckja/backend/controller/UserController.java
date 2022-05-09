package io.nonamuckja.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.nonamuckja.backend.domain.user.User;
import io.nonamuckja.backend.domain.user.UserRepository;
import io.nonamuckja.backend.dto.UserDTO;
import io.nonamuckja.backend.dto.UserRegisterFormDTO;
import io.nonamuckja.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

	private final UserService userService;

	private final UserRepository userRepository;

	@PostMapping("/register")
	public ResponseEntity<UserDTO> register(@RequestBody UserRegisterFormDTO userDTO) {
		log.info("User registration request received: {}", userDTO);

		Long registeredId = userService.register(userDTO);
		User user = userRepository.findById(registeredId).orElseThrow();
		log.info("User registration successful: {}", user);

		return ResponseEntity.ok(UserDTO.entityToDTO(user));
	}
}
