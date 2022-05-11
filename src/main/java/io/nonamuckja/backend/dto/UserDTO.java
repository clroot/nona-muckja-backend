package io.nonamuckja.backend.dto;

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

	private AddressDTO address;

	public static UserDTO fromEntity(User user) {
		UserDTO dto = new UserDTO();

		dto.id = user.getId();
		dto.username = user.getUsername();
		dto.email = user.getEmail();
		dto.address = AddressDTO.fromEntity(user.getAddress());

		return dto;
	}
}
