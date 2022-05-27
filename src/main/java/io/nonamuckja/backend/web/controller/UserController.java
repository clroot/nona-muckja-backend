package io.nonamuckja.backend.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.nonamuckja.backend.domain.user.UserRepository;
import io.nonamuckja.backend.security.AuthUserDTO;
import io.nonamuckja.backend.security.CheckLoginUser;
import io.nonamuckja.backend.service.UserService;
import io.nonamuckja.backend.web.dto.ProfileUpdateFormDTO;
import io.nonamuckja.backend.web.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {
	private final UserService userService;
	private final UserRepository userRepository;

	@PatchMapping("/profile")
	@CheckLoginUser
	public ResponseEntity<?> updateProfile(@RequestBody ProfileUpdateFormDTO profileUpdateFormDTO,
		@AuthUserDTO UserDTO userDTO) {
		log.info("Profile update request receive: UserDTO:{},Profile:{}", userDTO, profileUpdateFormDTO);

		userService.updateProfile(profileUpdateFormDTO, userDTO);

		log.info("Profile update successful:UserDTO:{},Profile:{}", userDTO, profileUpdateFormDTO);

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
