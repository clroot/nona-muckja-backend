package io.nonamuckja.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import io.bloco.faker.Faker;
import io.nonamuckja.backend.domain.Address;
import io.nonamuckja.backend.domain.user.User;
import io.nonamuckja.backend.dto.AddressDTO;
import io.nonamuckja.backend.dto.UserRegisterFormDTO;

@Component
public class TestUtils {
	@Autowired
	private PasswordEncoder passwordEncoder;

	public User createUser() {
		Faker faker = new Faker();

		return User.builder()
			.email(faker.internet.email())
			.username(faker.internet.userName())
			.password(passwordEncoder.encode(faker.internet.password()))
			.address(createAddress())
			.build();
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
