package io.nonamuckja.backend.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.nonamuckja.backend.domain.user.User;
import io.nonamuckja.backend.domain.user.UserRepository;
import io.nonamuckja.backend.domain.user.UserRole;
import io.nonamuckja.backend.dto.AddressDTO;
import io.nonamuckja.backend.dto.UserRegisterFormDTO;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	@Transactional
	public Long register(UserRegisterFormDTO userRegisterFormDTO) {
		validateDuplicateUser(userRegisterFormDTO);

		String username = userRegisterFormDTO.getUsername();
		String password = passwordEncoder.encode(userRegisterFormDTO.getPassword());
		String email = userRegisterFormDTO.getEmail();
		AddressDTO address = userRegisterFormDTO.getAddress();

		User user = User.builder()
			.username(username)
			.password(password)
			.email(email)
			.address(address.toEntity())
			.build();

		user.addRole(UserRole.USER);

		userRepository.save(user);

		return user.getId();
	}

	private void validateDuplicateUser(UserRegisterFormDTO userRegisterFormDTO) {
		Optional<User> result = userRepository.findByUsername(userRegisterFormDTO.getUsername());
		if (result.isPresent()) {
			throw new IllegalStateException("이미 존재하는 사용자입니다.");
		}
	}
}
