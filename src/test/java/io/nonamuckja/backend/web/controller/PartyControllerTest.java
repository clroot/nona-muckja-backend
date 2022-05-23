package io.nonamuckja.backend.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.annotation.PostConstruct;

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
import io.nonamuckja.backend.domain.party.Party;
import io.nonamuckja.backend.domain.party.PartyStatus;
import io.nonamuckja.backend.domain.user.User;
import io.nonamuckja.backend.exception.UserDuplicateException;
import io.nonamuckja.backend.web.dto.AddressDTO;
import io.nonamuckja.backend.web.dto.PartyRegisterFormDTO;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class PartyControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private TestUtils testUtils;

	@PostConstruct
	void setUp() {
		String email = "testParty@email.com";
		String username = "testParty";
		String password = "test-password";

		try {
			testUtils.createUser(email, username, password);
		} catch (UserDuplicateException e) {
			// do nothing
		}
	}

	@Test
	@DisplayName("POST /api/v1/party 성공 테스트")
	@WithUserDetails(value = "testParty")
	public void testRegisterParty() throws Exception {
		//given
		String url = "/api/v1/party";

		AddressDTO address = testUtils.createAddressDTO();
		Long limitMemberCount = 10L;

		PartyRegisterFormDTO registerFormDTO = PartyRegisterFormDTO.builder()
			.address(address)
			.limitMemberCount(limitMemberCount)
			.build();

		//when & then
		mockMvc.perform(post(url)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(registerFormDTO)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").isNumber())
			.andExpect(jsonPath("$.host.id").isNumber())
			.andExpect(jsonPath("$.address").exists())
			.andExpect(jsonPath("$.address.roadAddress").value(address.getRoadAddress()))
			.andExpect(jsonPath("$.address.x").value(address.getX()))
			.andExpect(jsonPath("$.address.y").value(address.getY()))
			.andExpect(jsonPath("$.limitMemberCount").value(limitMemberCount))
			.andExpect(jsonPath("$.currentMemberCount").value(1L))
			.andExpect(jsonPath("$.status").value(PartyStatus.OPEN.name()))
			.andExpect(jsonPath("$.members").isArray())
			.andReturn().getResponse();
	}

	@Test
	@DisplayName("POST /api/v1/party/{id}/join 성공 테스트")
	@WithUserDetails(value = "testParty")
	public void test() throws Exception {
		//given
		User host = testUtils.createUser();
		Party party = testUtils.createParty(host, 10L);
		Long partyId = party.getId();
		String url = "/api/v1/party/{id}/join".replace("{id}", partyId.toString());

		//when & then
		var res = mockMvc.perform(post(url))
			.andExpect(status().isOk())
			.andReturn().getResponse();

		System.out.println("result: " + res.getContentAsString());
	}
}
