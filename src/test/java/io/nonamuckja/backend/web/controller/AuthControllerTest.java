package io.nonamuckja.backend.web.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.nonamuckja.backend.TestUtils;
import io.nonamuckja.backend.domain.user.UserRepository;
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

	@AfterEach
	void tearDown() {
		userRepository.deleteAll();
	}

	@Test
	@DisplayName("/register 테스트")
	public void register() throws Exception {
		//given
		long beforeCount = userRepository.count();
		UserRegisterFormDTO userRegisterFormDTO = testUtils.createUserRegisterFormDTO();
		String url = "/api/v1/auth/register";

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
	@DisplayName("/login 테스트")
	public void testLoginEndpoint() throws Exception {
		//given
		UserRegisterFormDTO registerFormDTO = testUtils.createUserRegisterFormDTO();
		testUtils.createUser(registerFormDTO);

		UserLoginFormDTO loginFormDTO = UserLoginFormDTO.builder()
			.username(registerFormDTO.getUsername())
			.password(registerFormDTO.getPassword())
			.build();

		String url = "/api/v1/auth/login";

		//when & then
		mockMvc.perform(post(url)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(loginFormDTO)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("accessToken").exists());
	}

	@Test
	@DisplayName("/login 실패 테스트")
	public void test() throws Exception {
		//given
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
		String url = "/api/v1/auth/login";

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
}
