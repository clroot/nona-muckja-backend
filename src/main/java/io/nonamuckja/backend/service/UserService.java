package io.nonamuckja.backend.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import io.nonamuckja.backend.domain.user.User;
import io.nonamuckja.backend.domain.user.UserRepository;
import io.nonamuckja.backend.exception.UserNotFoundException;
import io.nonamuckja.backend.web.dto.AddressDTO;
import io.nonamuckja.backend.web.dto.UserDTO;
import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	@Transactional
	public void updatePicture(String picture, UserDTO userDTO) {
		User user = getUserEntity(userDTO.getId());
		user.updatePicture(picture);
	}

	@Transactional
	public void updateAddress(AddressDTO addressDTO, UserDTO userDTO) {
		User user = getUserEntity(userDTO.getId());
		user.updateAddress(addressDTO.toEntity());
	}

	private User getUserEntity(Long userId) {
		return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
	}
}
