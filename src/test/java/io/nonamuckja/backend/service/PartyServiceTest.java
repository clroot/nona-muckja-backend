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
import io.nonamuckja.backend.exception.PartyJoinException;
import io.nonamuckja.backend.exception.PartyLeaveException;
import io.nonamuckja.backend.web.dto.PartyRegisterFormDTO;
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
		PartyRegisterFormDTO createFormDTO = PartyRegisterFormDTO.builder()
			.address(testUtils.createAddress())
			.limitMemberCount(10L)
			.build();

		//when
		Long createdPartyId = partyService.createParty(createFormDTO, UserDTO.fromEntity(testUser));

		//then
		Party createdParty = partyRepository.findById(createdPartyId).orElseThrow();
		assertEquals(1, createdParty.getMembers().size());
		assertEquals(createFormDTO.getAddress().getRoadAddress(), createdParty.getAddress().getRoadAddress());
		assertEquals(createFormDTO.getLimitMemberCount(), createdParty.getLimitMemberCount());
		assertEquals(testUser.getId(), createdParty.getHost().getId());
		assertEquals(PartyStatus.OPEN, createdParty.getStatus());
	}

	@Test
	@DisplayName("파티 참여 성공 테스트")
	public void testPartyJoin() {
		//given
		User hostUser = testUtils.createUser();
		User joinUser = testUtils.createUser();
		Party party = testUtils.createParty(hostUser, 10L);

		//when
		partyService.joinMember(party.getId(), UserDTO.fromEntity(joinUser));

		//then
		assertTrue(party.getMembers().stream()
			.map(partyUser -> partyUser.getUser().getId())
			.anyMatch(userId -> userId.equals(joinUser.getId())));
	}

	@Test
	@DisplayName("파티 참여 실패 테스트")
	public void testPartyJoinFail() {
		//given
		User hostUser = testUtils.createUser();
		User joinUser1 = testUtils.createUser();
		User joinUser2 = testUtils.createUser();
		Party party = testUtils.createParty(hostUser, 2L);

		//when
		partyService.joinMember(party.getId(), UserDTO.fromEntity(joinUser1));

		//then
		assertThrows(PartyJoinException.class,
			() -> partyService.joinMember(party.getId(), UserDTO.fromEntity(joinUser1)));
		assertThrows(PartyJoinException.class,
			() -> partyService.joinMember(party.getId(), UserDTO.fromEntity(joinUser2)));
	}

	@Test
	@DisplayName("파티 탈퇴 성공 테스트")
	public void testLeaveMember() {
		//given
		User hostUser = testUtils.createUser();
		User joinUser = testUtils.createUser();
		Party party = testUtils.createParty(hostUser, 2L);

		partyService.joinMember(party.getId(), UserDTO.fromEntity(joinUser));

		//when
		partyService.leaveMember(party.getId(), UserDTO.fromEntity(joinUser));

		//then
		assertTrue(party.getMembers().stream()
			.map(partyUser -> partyUser.getUser().getId())
			.noneMatch(userId -> userId.equals(joinUser.getId())));
	}

	@Test
	@DisplayName("파티 탈퇴 실패 테스트")
	public void testLeaveMemberFail() {
		//given
		User hostUser = testUtils.createUser();
		User notJoinUser = testUtils.createUser();
		Party party = testUtils.createParty(hostUser, 2L);

		//when & then
		assertThrows(PartyLeaveException.class,
			() -> partyService.leaveMember(party.getId(), UserDTO.fromEntity(notJoinUser)));
	}

	@Test
	@DisplayName("파티 배달 시작 테스트")
	public void testStartDelivery() {
		//given
		User hostUser = testUtils.createUser();
		User notHostUser = testUtils.createUser();
		Party party = testUtils.createParty(hostUser, 2L);
		Long partyId = party.getId();

		//when
		partyService.startDelivery(partyId, UserDTO.fromEntity(hostUser));

		//then
		assertEquals(PartyStatus.DELIVERING, party.getStatus());
		assertThrows(IllegalStateException.class,
			() -> partyService.startDelivery(partyId, UserDTO.fromEntity(notHostUser)));
	}

	@Test
	@DisplayName("파티 배달 완료 테스트")
	public void testFinishDelivery() {
		//given
		User hostUser = testUtils.createUser();
		User notHostUser = testUtils.createUser();
		Party party = testUtils.createParty(hostUser, 2L);
		Long partyId = party.getId();

		//when
		partyService.finishDelivery(partyId, UserDTO.fromEntity(hostUser));

		//then
		assertEquals(PartyStatus.DELIVERED, party.getStatus());
		assertThrows(IllegalStateException.class,
			() -> partyService.finishDelivery(partyId, UserDTO.fromEntity(notHostUser)));
	}

	@Test
	@DisplayName("파티 종료 테스트")
	public void testFinishParty() {
		//given
		User hostUser = testUtils.createUser();
		User notHostUser = testUtils.createUser();
		Party party = testUtils.createParty(hostUser, 2L);
		Long partyId = party.getId();

		//when
		partyService.finishParty(partyId, UserDTO.fromEntity(hostUser));

		//then
		assertEquals(PartyStatus.FINISHED, party.getStatus());
		assertThrows(IllegalStateException.class,
			() -> partyService.finishParty(partyId, UserDTO.fromEntity(notHostUser)));
	}

	@Test
	@DisplayName("파티 취소 테스트")
	public void testCancelParty() {
		//given
		User hostUser = testUtils.createUser();
		User notHostUser = testUtils.createUser();
		Party party = testUtils.createParty(hostUser, 2L);
		Long partyId = party.getId();

		//when
		partyService.cancelParty(partyId, UserDTO.fromEntity(hostUser));

		//then
		assertEquals(PartyStatus.CANCELED, party.getStatus());
		assertThrows(IllegalStateException.class,
			() -> partyService.cancelParty(partyId, UserDTO.fromEntity(notHostUser)));
	}
}
