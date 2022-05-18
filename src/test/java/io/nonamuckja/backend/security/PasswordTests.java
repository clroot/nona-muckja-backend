package io.nonamuckja.backend.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.github.javafaker.Faker;

@SpringBootTest
public class PasswordTests {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	@DisplayName("패스워드 인코더 테스트")
	public void passwordEncoderTest() {
		//given
		Faker faker = new Faker();
		String password = faker.internet().password();

		//when
		String encodedPassword = passwordEncoder.encode(password);
		boolean isPasswordMatch = passwordEncoder.matches(password, encodedPassword);

		//then
		assertTrue(isPasswordMatch);
		assertNotEquals(password, encodedPassword);
	}
}
