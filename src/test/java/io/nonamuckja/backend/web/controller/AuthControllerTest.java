package io.nonamuckja.backend.web.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.nonamuckja.backend.TestUtils;
import io.nonamuckja.backend.domain.user.UserRepository;
import io.nonamuckja.backend.exception.UserDuplicateException;
import io.nonamuckja.backend.web.dto.UserLoginFormDTO;
import io.nonamuckja.backend.web.dto.UserRegisterFormDTO;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class AuthControllerTest {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private TestUtils testUtils;

	@PostConstruct
	void setUp() {
		String email = "testAuth@email.com";
		String username = "testAuth";
		String password = "test-password";
		try {
			testUtils.createUser(email, username, password);
		} catch (UserDuplicateException e) {
			// do nothing
		}
	}

	@Test
	@DisplayName("POST /api/v1/auth/register 성공 테스트")
	public void registerSuccess() throws Exception {
		//given
		String url = "/api/v1/auth/register";

		long beforeCount = userRepository.count();
		UserRegisterFormDTO userRegisterFormDTO = testUtils.createUserRegisterFormDTO();

		//when
		mockMvc.perform(post(url)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(userRegisterFormDTO)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("id").exists());

		//then
		assertEquals(beforeCount + 1, userRepository.count());
	}

	@Test
	@DisplayName("POST /api/v1/auth/register 실패 테스트")
	@WithUserDetails("testAuth")
	public void registerFail() throws Exception {
		//given
		String url = "/api/v1/auth/register";

		UserRegisterFormDTO userRegisterFormDTO = testUtils.createUserRegisterFormDTO();

		//when & then
		mockMvc.perform(post(url)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(userRegisterFormDTO)))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("POST /api/v1/auth/login 성공 테스트")
	public void loginSuccess() throws Exception {
		//given
		String url = "/api/v1/auth/login";

		UserRegisterFormDTO registerFormDTO = testUtils.createUserRegisterFormDTO();
		testUtils.createUser(registerFormDTO);

		UserLoginFormDTO loginFormDTO = UserLoginFormDTO.builder()
			.username(registerFormDTO.getUsername())
			.password(registerFormDTO.getPassword())
			.build();

		//when & then
		mockMvc.perform(post(url)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(loginFormDTO)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("accessToken").exists());
	}

	@Test
	@DisplayName("POST /api/v1/auth/login 실패 테스트")
	public void loginFail() throws Exception {
		//given
		String url = "/api/v1/auth/login";

		UserRegisterFormDTO registerFormDTO = testUtils.createUserRegisterFormDTO();
		testUtils.createUser(registerFormDTO);

		UserLoginFormDTO wrongUsernameLoginDTO = UserLoginFormDTO.builder()
			.username(StringUtils.reverse(registerFormDTO.getUsername()))
			.password(registerFormDTO.getPassword())
			.build();

		UserLoginFormDTO wrongPasswordLoginDTO = UserLoginFormDTO.builder()
			.username(registerFormDTO.getUsername())
			.password(StringUtils.reverse(registerFormDTO.getPassword()))
			.build();

		//when & then
		mockMvc.perform(post(url)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(wrongUsernameLoginDTO)))
			.andExpect(status().isBadRequest());

		mockMvc.perform(post(url)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(wrongPasswordLoginDTO)))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("POST /api/v1/auth/login 실패 테스트 2 - 중복 로그인")
	@WithUserDetails("testAuth")
	public void loginFail2() throws Exception {
		//given
		String url = "/api/v1/auth/login";

		UserRegisterFormDTO registerFormDTO = testUtils.createUserRegisterFormDTO();
		testUtils.createUser(registerFormDTO);

		UserLoginFormDTO loginFormDTO = UserLoginFormDTO.builder()
			.username(registerFormDTO.getUsername())
			.password(registerFormDTO.getPassword())
			.build();

		//when & then
		mockMvc.perform(post(url)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(loginFormDTO)))
			.andExpect(status().isUnauthorized());
	}
}
