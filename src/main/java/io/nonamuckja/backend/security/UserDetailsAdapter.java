package io.nonamuckja.backend.security;

import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import io.nonamuckja.backend.domain.user.User;
import io.nonamuckja.backend.dto.UserDTO;
import lombok.Getter;

@Getter
public class UserDetailsAdapter extends org.springframework.security.core.userdetails.User {
	private final UserDTO userDTO;

	public UserDetailsAdapter(User user) {
		super(
			user.getUsername(),
			user.getPassword(),
			user.getRoles()
				.stream()
				.map(role -> new SimpleGrantedAuthority(role.getKey()))
				.collect(Collectors.toList())
		);
		this.userDTO = UserDTO.fromEntity(user);
	}
}
