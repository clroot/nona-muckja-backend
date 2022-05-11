package io.nonamuckja.backend.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

import io.bloco.faker.Faker;
import io.nonamuckja.backend.domain.user.UserRepository;
import io.nonamuckja.backend.dto.UserRegisterFormDTO;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class UserControllerTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("/register 테스트")
	public void register() throws Exception {
		//given
		long beforeCount = userRepository.count();
		UserRegisterFormDTO userRegisterFormDTO = createUserRegisterFormDTO();
		String url = "/api/user/register";

		//when
		mockMvc.perform(post(url)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(userRegisterFormDTO)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("id").exists());

		//then
		assertEquals(beforeCount + 1, userRepository.count());
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
			.build();
	}
}