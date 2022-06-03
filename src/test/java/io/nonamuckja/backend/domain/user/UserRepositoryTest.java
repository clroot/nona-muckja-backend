package io.nonamuckja.backend.domain.user;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import io.nonamuckja.backend.TestUtils;
import io.nonamuckja.backend.web.dto.UserRegisterFormDTO;

@SpringBootTest
@Transactional
class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TestUtils testUtils;

	@Test
	@DisplayName("유저_저장_테스트")
	public void testUserSave() {
		//given
		UserRegisterFormDTO registerFormDTO = testUtils.createUserRegisterFormDTO();

		String username = registerFormDTO.getUsername();
		String email = registerFormDTO.getEmail();
		String password = registerFormDTO.getPassword();

		userRepository.save(User.builder()
			.email(email)
			.username(username)
			.password(password)
			.build());

		//when
		User user = userRepository.findByEmail(email).orElseThrow();

		//then
		assertEquals(email, user.getEmail());
		assertEquals(username, user.getUsername());
	}

	@Test
	@DisplayName("findByEmail 테스트")
	public void findByEmail() {
		//given
		UserRegisterFormDTO registerFormDTO = testUtils.createUserRegisterFormDTO();

		String username = registerFormDTO.getUsername();
		String email = registerFormDTO.getEmail();
		String password = registerFormDTO.getPassword();

		userRepository.save(User.builder()
			.email(email)
			.username(username)
			.password(password)
			.build());

		//when
		User user = userRepository.findByEmail(email).orElseThrow();

		//then
		assertEquals(email, user.getEmail());
		assertEquals(username, user.getUsername());
	}

	@Test
	@DisplayName("findByUsername 테스트")
	@Transactional
	public void findByUsername() {
		//given
		UserRegisterFormDTO registerFormDTO = testUtils.createUserRegisterFormDTO();

		String username = registerFormDTO.getUsername();
		String email = registerFormDTO.getEmail();
		String password = registerFormDTO.getPassword();

		userRepository.save(User.builder()
			.email(email)
			.username(username)
			.password(password)
			.build());

		//when
		User user = userRepository.findByUsername(username).orElseThrow();

		//then
		assertEquals(email, user.getEmail());
		assertEquals(username, user.getUsername());
	}

	@Test
	@DisplayName("findAll() 테스트")
	public void testFindAll() {
		//given
		IntStream.range(0, 50).forEach(i -> {
			testUtils.createUser();
		});
		Pageable pageable = PageRequest.of(0, 30);

		//when
		System.out.println("========== findAll query start. ==========");
		var users = userRepository.findAll(pageable);
		System.out.println("========== findAll query end. ==========");

		//then
		assertNotNull(users);
		assertEquals(30, users.getSize());
	}
}
