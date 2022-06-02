package io.nonamuckja.backend.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.nonamuckja.backend.TestUtils;
import io.nonamuckja.backend.domain.Address;
import io.nonamuckja.backend.domain.Coordinate;
import io.nonamuckja.backend.domain.party.FoodCategory;
import io.nonamuckja.backend.domain.party.Party;
import io.nonamuckja.backend.domain.party.PartyRepository;
import io.nonamuckja.backend.domain.party.PartyStatus;
import io.nonamuckja.backend.domain.user.User;
import io.nonamuckja.backend.exception.UserDuplicateException;
import io.nonamuckja.backend.web.dto.PartyRegisterFormDTO;
import io.nonamuckja.backend.web.dto.PartySearchRequestDTO;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@AutoConfigureMockMvc
class PartyControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PartyRepository partyRepository;
	@Autowired
	private TestUtils testUtils;

	private Coordinate center;

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

	@BeforeEach
	void setUpEach() {
		partyRepository.deleteAll();
		User host = testUtils.createUser();
		Address partyAddress = testUtils.createAddress();
		center = partyAddress.getCoordinate();

		IntStream.range(0, 100).forEach(i -> {
			Coordinate coordinate;
			if (i < 20) {
				coordinate = center.getVertex(0.19).getLeft();
			} else if (i < 50) {
				coordinate = center.getVertex(0.49).getLeft();
			} else if (i < 80) {
				coordinate = center.getVertex(0.79).getLeft();
			} else {
				coordinate = center.getVertex(0.9).getLeft();
			}
			Address address = Address.builder()
				.address("서울시 강남구 역삼동")
				.roadAddress("서울시 강남구 역삼동")
				.zipCode("12345")
				.coordinate(coordinate)
				.build();

			testUtils.createParty(host, 10L, address);
		});
	}

	@Test
	@DisplayName("POST /api/v1/party/search 성공 테스트")
	@WithUserDetails(value = "testParty")
	public void testSearchParty() throws Exception {
		//given
		String url = "/api/v1/party/search";
		PartySearchRequestDTO searchRequestDTO = PartySearchRequestDTO.builder()
			.clientLocation(center)
			.radius(0.5)
			.status(PartyStatus.OPEN)
			.build();

		//when & then
		var result = mockMvc.perform(post(url)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(searchRequestDTO)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.totalElements").value(50))
			.andReturn().getResponse().getContentAsString();
	}

	@Test
	@DisplayName("POST /api/v1/party 성공 테스트")
	@WithUserDetails(value = "testParty")
	public void testRegisterParty() throws Exception {
		//given
		String url = "/api/v1/party";

		String title = "테스트 파티";
		String description = "테스트 파티 설명";
		Address address = testUtils.createAddress();
		Long limitMemberCount = 10L;

		PartyRegisterFormDTO registerFormDTO = PartyRegisterFormDTO.builder()
			.title(title)
			.description(description)
			.address(address)
			.limitMemberCount(limitMemberCount)
			.foodCategory(FoodCategory.ASIAN_FOOD)
			.build();

		//when & then
		mockMvc.perform(post(url)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(registerFormDTO)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").isNumber())
			.andExpect(jsonPath("$.host.id").isNumber())
			.andExpect(jsonPath("$.title").value(title))
			.andExpect(jsonPath("$.description").value(description))
			.andExpect(jsonPath("$.address").exists())
			.andExpect(jsonPath("$.address.roadAddress").value(address.getRoadAddress()))
			.andExpect(jsonPath("$.limitMemberCount").value(limitMemberCount))
			.andExpect(jsonPath("$.currentMemberCount").value(1L))
			.andExpect(jsonPath("$.status").value(PartyStatus.OPEN.name()))
			.andExpect(jsonPath("$.members").isArray())
			.andExpect(jsonPath("$.foodCategory").value(FoodCategory.ASIAN_FOOD.name()));
	}

	@Test
	@DisplayName("POST /api/v1/party/{id}/join 성공 테스트")
	@WithUserDetails(value = "testParty")
	public void testJoinParty() throws Exception {
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
