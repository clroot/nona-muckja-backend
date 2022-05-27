package io.nonamuckja.backend.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import io.nonamuckja.backend.domain.user.User;
import io.nonamuckja.backend.domain.user.UserRepository;
import io.nonamuckja.backend.exception.UserNotFoundException;
import io.nonamuckja.backend.web.dto.ProfileUpdateFormDTO;
import io.nonamuckja.backend.web.dto.UserDTO;
import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

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
