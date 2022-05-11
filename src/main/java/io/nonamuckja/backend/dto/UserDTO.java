package io.nonamuckja.backend.dto;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import io.nonamuckja.backend.domain.Address;
import io.nonamuckja.backend.domain.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/*
    TODO: User 클래스 상속으로 인해, password 등의 필드가 클라이언트로 전달되고 있음
    - UserDetails 객체 전달을 위한 별개의 클래스를 생성하여, password 등의 필드를 제거하도록 해야함
    - OAuth2 연동 이후, 클래스 분리 작업 진행 필요
 */
public class UserDTO extends org.springframework.security.core.userdetails.User {

	private Long id;

	private String email;

	private Address address;

	private UserDTO(String username, String password,
		Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}

	public static UserDTO entityToDTO(User user) {
		String username = user.getUsername();
		String password = user.getPassword();
		Collection<? extends GrantedAuthority> authorities =
			user.getRoles()
				.stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
				.collect(Collectors.toList());

		UserDTO userDTO = new UserDTO(username, password, authorities);

		userDTO.id = user.getId();
		userDTO.email = user.getEmail();
		userDTO.address = user.getAddress();

		return userDTO;
	}
}
