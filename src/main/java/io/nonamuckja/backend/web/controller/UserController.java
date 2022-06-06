package io.nonamuckja.backend.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.nonamuckja.backend.domain.user.UserRole;
import io.nonamuckja.backend.security.AuthUserDTO;
import io.nonamuckja.backend.security.CheckLoginUser;
import io.nonamuckja.backend.service.PartyService;
import io.nonamuckja.backend.service.UserService;
import io.nonamuckja.backend.web.dto.PartyDTO;
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
	private final PartyService partyService;

	@GetMapping
	@CheckLoginUser
	public ResponseEntity<Page<UserDTO>> index(
		@AuthUserDTO UserDTO userDTO, HttpServletResponse response, Pageable pageable) throws IOException {
		log.info("User list inquire request receive: UserDTO: {}", userDTO);
		if (!isAdminUser(userDTO)) {
			log.info("User doesn't have ADMIN role, redirect user's profile: UserDTO: {}", userDTO);
			response.sendRedirect("/api/v1/user/" + userDTO.getId());
			return null;
		}

		var users = userService.list(pageable);
		log.info("User list inquire successful: UserDTO:{}", userDTO);

		return new ResponseEntity<>(users, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	@CheckLoginUser
	public ResponseEntity<UserDTO> getProfile(@PathVariable("id") Long userId) {
		log.info("Profile inquire request receive: UserID: {}", userId);
		var userDTO = userService.findById(userId);
		log.info("Profile inquire successful: UserID: {}", userId);

		return new ResponseEntity<>(userDTO, HttpStatus.OK);
	}

	@GetMapping("/{id}/party")
	@CheckLoginUser
	public ResponseEntity<Page<PartyDTO>> getParty(@PathVariable("id") Long userId, Pageable pageable) {
		log.info("Party inquire by member request receive: UserID: {}", userId);
		var result = partyService.getPartyByMember(userId, pageable);
		log.info("Party inquire by member successful: UserID: {}", userId);

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@PatchMapping("/profile")
	@CheckLoginUser
	public ResponseEntity<?> updateProfile(@RequestBody ProfileUpdateFormDTO profileUpdateFormDTO,
		@AuthUserDTO UserDTO userDTO) {
		log.info("Profile update request receive: UserDTO:{},Profile:{}", userDTO, profileUpdateFormDTO);
		userService.updateProfile(profileUpdateFormDTO, userDTO);
		log.info("Profile update successful:UserDTO:{},Profile:{}", userDTO, profileUpdateFormDTO);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private boolean isAdminUser(UserDTO userDTO) {
		return userDTO.getRoles()
			.stream()
			.anyMatch(userRole -> (userRole == UserRole.ADMIN));
	}
}
