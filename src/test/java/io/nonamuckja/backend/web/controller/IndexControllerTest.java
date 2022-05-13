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
		testUtils.createUser("test@email.com", "test", "password");
	}

	@Test
	@DisplayName("@AuthUserDTO 테스트")
	@WithUserDetails(value = "test")
	public void testAuthUserDTO() throws Exception {
		//given
		String url = "/auth";

		//when
		String result = mockMvc.perform(get(url))
			.andReturn().getResponse().getContentAsString();

		//then
		assertEquals("test", result);
	}
}
