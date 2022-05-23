package io.nonamuckja.backend.web.dto;

import io.nonamuckja.backend.domain.Address;
import io.nonamuckja.backend.domain.user.User;
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

	public static UserDTO fromEntity(User user) {
		UserDTO dto = new UserDTO();

		dto.id = user.getId();
		dto.username = user.getUsername();
		dto.email = user.getEmail();
		dto.address = user.getAddress();

		return dto;
	}

	public User toEntity() {
		return User.builder()
			.id(id)
			.username(username)
			.email(email)
			.address(address)
			.build();
	}
}
