package io.nonamuckja.backend.web.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import io.nonamuckja.backend.TestUtils;
import io.nonamuckja.backend.exception.UserDuplicateException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class IndexControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TestUtils testUtils;

	@PostConstruct
	void setUp() {
		String email = "testIndex@email.com";
		String username = "testIndex";
		String password = "password";

		try {
			testUtils.createUser(email, username, password);
		} catch (UserDuplicateException e) {
			// do nothing
		}
	}

	@Test
	@DisplayName("@AuthUserDTO 테스트")
	@WithUserDetails(value = "testIndex")
	public void testAuthUserDTO() throws Exception {
		//given
		String url = "/auth";

		//when
		String result = mockMvc.perform(get(url))
			.andReturn().getResponse().getContentAsString();

		//then
		assertEquals("testIndex", result);
	}
}
