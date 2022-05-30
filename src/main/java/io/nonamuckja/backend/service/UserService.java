package io.nonamuckja.backend.service;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.nonamuckja.backend.domain.user.User;
import io.nonamuckja.backend.domain.user.UserRepository;
import io.nonamuckja.backend.exception.UserNotFoundException;
import io.nonamuckja.backend.web.dto.ProfileUpdateFormDTO;
import io.nonamuckja.backend.web.dto.UserDTO;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	public Page<UserDTO> list(Pageable pageable) {
		var users = userRepository.findAll(pageable);
		return new PageImpl<>(
			users.stream()
				.map(UserDTO::fromEntity)
				.collect(Collectors.toList()),
			pageable,
			users.getTotalElements()
		);
	}

	public UserDTO findById(Long userId) {
		return UserDTO.fromEntity(getUserEntity(userId));
	}

	@Transactional
	public void updateProfile(ProfileUpdateFormDTO dto, UserDTO userDTO) {
		User user = getUserEntity(userDTO.getId());
		if (dto.getPicture() != null) {
			user.updatePicture(dto.getPicture());
		}
		if (dto.getAddress() != null) {
			user.updateAddress(dto.getAddress());
		}

	}

	private User getUserEntity(Long userId) {
		return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
	}
}
