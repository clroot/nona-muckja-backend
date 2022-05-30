package io.nonamuckja.backend.web.dto;

import java.util.Set;

import io.nonamuckja.backend.domain.Address;
import io.nonamuckja.backend.domain.user.User;
import io.nonamuckja.backend.domain.user.UserRole;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDTO {

	private Long id;

	private String username;

	private String email;

	private Address address;

	private Set<UserRole> roles;

	public static UserDTO fromEntity(User user) {
		UserDTO dto = new UserDTO();

		dto.id = user.getId();
		dto.username = user.getUsername();
		dto.email = user.getEmail();
		dto.address = user.getAddress();
		dto.roles = user.getRoles();

		return dto;
	}

	public User toEntity() {
		return User.builder()
			.id(id)
			.username(username)
			.email(email)
			.address(address)
			.roles(roles)
			.build();
	}
}
