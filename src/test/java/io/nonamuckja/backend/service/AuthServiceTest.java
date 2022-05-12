package io.nonamuckja.backend.service;

import static org.junit.jupiter.api.Assertions.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import io.nonamuckja.backend.TestUtils;
import io.nonamuckja.backend.domain.user.User;
import io.nonamuckja.backend.domain.user.UserRepository;
import io.nonamuckja.backend.exception.AuthException;
import io.nonamuckja.backend.exception.UserDuplicateException;
import io.nonamuckja.backend.security.JwtTokenUtils;
import io.nonamuckja.backend.security.UserDetailsAdapter;
import io.nonamuckja.backend.web.dto.UserLoginFormDTO;
import io.nonamuckja.backend.web.dto.UserRegisterFormDTO;

@SpringBootTest
@Transactional
class AuthServiceTest {

	@PersistenceContext
	EntityManager em;
	@Autowired
	private AuthService authService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TestUtils testUtils;

	@Autowired
	private JwtTokenUtils jwtTokenUtils;

	@Test
	@DisplayName("로그인 성공 테스트")
	public void login() {
		//given
		UserRegisterFormDTO registerFormDTO = testUtils.createUserRegisterFormDTO();

		var username = registerFormDTO.getUsername();
		var password = registerFormDTO.getPassword();

		var registeredId = authService.register(registerFormDTO);

		User user = userRepository.findById(registeredId).orElseThrow();
		UserDetailsAdapter userDetails = new UserDetailsAdapter(user);

		UserLoginFormDTO loginFormDTO = UserLoginFormDTO.builder()
			.username(username)
			.password(password)
			.build();

		em.flush();
		em.clear();

		//when
		String token = authService.login(loginFormDTO);

		//then
		assertTrue(jwtTokenUtils.validateToken(token, userDetails));
		assertEquals(username, jwtTokenUtils.getUsernameFromToken(token));
	}

	@Test
	@DisplayName("로그인 실패 테스트")
	public void test() {
		// given
		UserRegisterFormDTO registerFormDTO = testUtils.createUserRegisterFormDTO();

		var username = registerFormDTO.getUsername();
		var password = registerFormDTO.getPassword();

		var incorrectUsername = StringUtils.reverse(username);
		var incorrectPassword = StringUtils.reverse(password);

		authService.register(registerFormDTO);

		// when
		UserLoginFormDTO incorrectUsernameLoginForm = UserLoginFormDTO.builder()
			.username(incorrectUsername)
			.password(password)
			.build();
		UserLoginFormDTO incorrectPasswordLoginForm = UserLoginFormDTO.builder()
			.username(username)
			.password(incorrectPassword)
			.build();

		// then
		assertThrows(AuthException.class, () -> authService.login(incorrectUsernameLoginForm));
		assertThrows(AuthException.class, () -> authService.login(incorrectPasswordLoginForm));
	}

	@Test
	@DisplayName("회원가입 테스트")
	public void register() {
		//given
		UserRegisterFormDTO userRegisterFormDTO = testUtils.createUserRegisterFormDTO();

		//when
		Long registeredUserId = authService.register(userRegisterFormDTO);

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
		UserRegisterFormDTO userRegisterFormDTO = testUtils.createUserRegisterFormDTO();

		//when
		authService.register(userRegisterFormDTO);

		//then
		assertThrows(UserDuplicateException.class,
			() -> authService.register(userRegisterFormDTO));
	}
}
