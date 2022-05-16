package io.nonamuckja.backend.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import io.nonamuckja.backend.TestUtils;
import io.nonamuckja.backend.domain.party.Party;
import io.nonamuckja.backend.domain.party.PartyRepository;
import io.nonamuckja.backend.domain.party.PartyStatus;
import io.nonamuckja.backend.domain.user.User;
import io.nonamuckja.backend.web.dto.PartyCreateFormDTO;
import io.nonamuckja.backend.web.dto.UserDTO;

@SpringBootTest
@Transactional
class PartyServiceTest {

	@Autowired
	private PartyService partyService;

	@Autowired
	private PartyRepository partyRepository;

	@Autowired
	private TestUtils testUtils;

	@Test
	@DisplayName("파티 생성 테스트")
	public void testCreateParty() {
		//given
		User testUser = testUtils.createUser();
		PartyCreateFormDTO createFormDTO = PartyCreateFormDTO.builder()
			.address(testUtils.createAddressDTO())
			.maxMemberLimit(10L)
			.build();

		//when
		Long createdPartyId = partyService.createParty(createFormDTO, UserDTO.fromEntity(testUser));

		//then
		Party createdParty = partyRepository.findById(createdPartyId).orElseThrow();
		assertEquals(createFormDTO.getAddress().getRoadAddress(), createdParty.getAddress().getRoadAddress());
		assertEquals(createFormDTO.getMaxMemberLimit(), createdParty.getMaxMemberLimit());
		assertEquals(testUser.getId(), createdParty.getHost().getId());
		assertEquals(PartyStatus.OPEN, createdParty.getStatus());
	}
}
