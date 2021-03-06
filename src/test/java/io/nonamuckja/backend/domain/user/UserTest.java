package io.nonamuckja.backend.domain.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.javafaker.Faker;

class UserTest {

	@Test
	@DisplayName("User 생성 테스트")
	public void createUserByBuilder() {
		//given
		Faker faker = new Faker();
		String testEmail = faker.internet().emailAddress();
		String testUsername = faker.name().username();

		//when
		User user = User.builder().email(testEmail).username(testUsername).build();

		//then
		assertEquals(testEmail, user.getEmail());
		assertEquals(testUsername, user.getUsername());
	}

	@Test
	@DisplayName("User Role 추가 테스트")
	public void addRole() {
		//given
		Faker faker = new Faker();
		String testEmail = faker.internet().emailAddress();
		String testUsername = faker.name().username();
		User user = User.builder().email(testEmail).username(testUsername).build();
		int beforeRoleCount = user.getRoles().size();

		//when
		user.addRole(UserRole.USER);

		//then
		int afterRoleCount = user.getRoles().size();
		assertEquals(beforeRoleCount + 1, afterRoleCount);
	}
}
