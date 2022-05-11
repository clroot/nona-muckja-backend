package io.nonamuckja.backend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.nonamuckja.backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BaseController {

	private final UserRepository userRepository;

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/auth")
	public String auth() {
		return "auth";
	}
}
