package io.nonamuckja.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.bloco.faker.Faker;
import io.nonamuckja.backend.domain.Address;
import io.nonamuckja.backend.domain.user.User;
import io.nonamuckja.backend.domain.user.UserRepository;
import io.nonamuckja.backend.domain.user.UserRole;
import io.nonamuckja.backend.web.dto.AddressDTO;
import io.nonamuckja.backend.web.dto.UserRegisterFormDTO;

@Component
@Transactional
public class TestUtils {
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	public User createUser() {
		Faker faker = new Faker();

		String email = faker.internet.email();
		String username = faker.internet.userName();
		String password = faker.internet.password();

		return createUser(email, username, password);
	}

	public User createUser(String email, String username, String password) {
		User user = User.builder()
			.email(email)
			.username(username)
			.password(passwordEncoder.encode(password))
			.address(createAddress())
			.build();

		user.addRole(UserRole.USER);
		userRepository.save(user);

		return user;
	}

	public void deleteUser(User user) {
		userRepository.delete(user);
	}

	public Address createAddress() {
		Faker faker = new Faker();

		return Address.builder()
			.address(faker.address.city() + " " + faker.address.secondaryAddress())
			.roadAddress(faker.address.streetAddress())
			.zipCode(faker.address.postcode())
			.x(Double.parseDouble(faker.number.decimal(2, 14)))
			.y(Double.parseDouble(faker.number.decimal(3, 14)))
			.build();
	}

	public UserRegisterFormDTO createUserRegisterFormDTO() {
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

	public AddressDTO createAddressDTO() {
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
