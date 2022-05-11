package io.nonamuckja.backend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.nonamuckja.backend.dto.UserDTO;
import io.nonamuckja.backend.security.AuthUserDTO;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BaseController {

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/auth")
	public String auth(@AuthUserDTO UserDTO userDTO) {
		return userDTO.getUsername();
	}
}
