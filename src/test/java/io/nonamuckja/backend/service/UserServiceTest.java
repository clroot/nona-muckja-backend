package io.nonamuckja.backend.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.bloco.faker.Faker;
import io.nonamuckja.backend.domain.user.User;
import io.nonamuckja.backend.domain.user.UserRepository;
import io.nonamuckja.backend.dto.AddressDTO;
import io.nonamuckja.backend.dto.UserRegisterFormDTO;
import io.nonamuckja.backend.exception.UserDuplicateException;

@SpringBootTest
class UserServiceTest {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Test
	@DisplayName("회원가입 테스트")
	public void register() {
		//given
		UserRegisterFormDTO userRegisterFormDTO = createUserRegisterFormDTO();

		//when
		Long registeredUserId = userService.register(userRegisterFormDTO);

		//then
		User registeredUser = userRepository.findById(registeredUserId).orElseThrow();
		assertEquals(userRegisterFormDTO.getUsername(), registeredUser.getUsername());
		assertEquals(userRegisterFormDTO.getEmail(), registeredUser.getEmail());
		assertNotEquals(userRegisterFormDTO.getPassword(), registeredUser.getPassword());
		assertNotNull(registeredUser.getRoles());
	}

	@Test
	@DisplayName("중복 회원가입 테스트")
	public void registerDuplicate() {
		//given
		UserRegisterFormDTO userRegisterFormDTO = createUserRegisterFormDTO();

		//when
		userService.register(userRegisterFormDTO);

		//then
		assertThrows(UserDuplicateException.class,
			() -> userService.register(userRegisterFormDTO));
	}

	private UserRegisterFormDTO createUserRegisterFormDTO() {
		Faker faker = new Faker();
		String username = faker.internet.userName();
		String password = faker.internet.password();
		String email = faker.internet.email();

		return UserRegisterFormDTO.builder()
			.username(username)
			.password(password)
			.email(email)
			.address(createAddressDTO())
			.build();
	}

	private AddressDTO createAddressDTO() {
		Faker faker = new Faker();
		return AddressDTO.builder()
			.address(faker.address.city() + " " + faker.address.secondaryAddress())
			.roadAddress(faker.address.streetAddress())
			.zipCode(faker.address.postcode())
			.x(Double.parseDouble(faker.number.decimal(2, 14)))
			.y(Double.parseDouble(faker.number.decimal(3, 14)))
			.build();
	}
}