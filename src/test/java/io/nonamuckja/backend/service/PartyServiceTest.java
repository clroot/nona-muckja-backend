package io.nonamuckja.backend.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import io.nonamuckja.backend.TestUtils;
import io.nonamuckja.backend.domain.Address;
import io.nonamuckja.backend.domain.Coordinate;
import io.nonamuckja.backend.domain.party.FoodCategory;
import io.nonamuckja.backend.domain.party.Party;
import io.nonamuckja.backend.domain.party.PartyRepository;
import io.nonamuckja.backend.domain.party.PartyStatus;
import io.nonamuckja.backend.domain.user.User;
import io.nonamuckja.backend.exception.PartyTransactionException;
import io.nonamuckja.backend.web.dto.PartyRegisterFormDTO;
import io.nonamuckja.backend.web.dto.PartyUpdateRequestDTO;
import io.nonamuckja.backend.web.dto.UserDTO;

@SpringBootTest
class PartyServiceTest {

	@Autowired
	private PartyService partyService;

	@Autowired
	private PartyRepository partyRepository;

	@Autowired
	private TestUtils testUtils;

	@Test
	@Transactional
	@DisplayName("파티 생성 테스트")
	public void testCreateParty() {
		//given
		String title = "테스트 파티";
		String description = "테스트 파티 설명";

		User testUser = testUtils.getRandomUser();
		FoodCategory foodCategory = testUtils.getRandomFoodCategory();
		PartyRegisterFormDTO createFormDTO = PartyRegisterFormDTO.builder()
			.title(title)
			.description(description)
			.address(testUtils.createAddress())
			.limitMemberCount(10L)
			.foodCategory(foodCategory)
			.partyTime(testUtils.getRandomFutureTime())
			.build();

		//when
		Long createdPartyId = partyService.createParty(createFormDTO, UserDTO.fromEntity(testUser));

		//then
		Party createdParty = partyRepository.findById(createdPartyId).orElseThrow();

		assertEquals(title, createdParty.getTitle());
		assertEquals(description, createdParty.getDescription());
		assertEquals(1, createdParty.getMembers().size());
		assertEquals(createFormDTO.getAddress().getRoadAddress(), createdParty.getAddress().getRoadAddress());
		assertEquals(createFormDTO.getLimitMemberCount(), createdParty.getLimitMemberCount());
		assertEquals(testUser.getId(), createdParty.getHost().getId());
		assertEquals(PartyStatus.OPEN, createdParty.getStatus());
		assertEquals(foodCategory, createdParty.getFoodCategory());
	}

	@Test
	@Transactional
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
	@Transactional
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
		assertThrows(PartyTransactionException.class,
			() -> partyService.joinMember(party.getId(), UserDTO.fromEntity(joinUser1)));
		assertThrows(PartyTransactionException.class,
			() -> partyService.joinMember(party.getId(), UserDTO.fromEntity(joinUser2)));
	}

	@Test
	@Transactional
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
	@Transactional
	@DisplayName("파티 탈퇴 실패 테스트")
	public void testLeaveMemberFail() {
		//given
		User hostUser = testUtils.createUser();
		User notJoinUser = testUtils.createUser();
		Party party = testUtils.createParty(hostUser, 2L);

		//when & then
		assertThrows(PartyTransactionException.class,
			() -> partyService.leaveMember(party.getId(), UserDTO.fromEntity(notJoinUser)));
	}

	@Test
	@Transactional
	@DisplayName("파티 배달 시작 테스트")
	public void testStartDelivery() {
		//given
		User hostUser = testUtils.createUser();
		User notHostUser = testUtils.createUser();
		Party party = testUtils.createParty(hostUser, 2L);
		Long partyId = party.getId();
		PartyUpdateRequestDTO requestDTO = PartyUpdateRequestDTO.builder()
			.status(PartyStatus.DELIVERING)
			.build();

		//when
		partyService.updateParty(partyId, requestDTO, UserDTO.fromEntity(hostUser));

		//then
		assertEquals(PartyStatus.DELIVERING, party.getStatus());
		assertThrows(IllegalStateException.class,
			() -> partyService.updateParty(partyId, requestDTO, UserDTO.fromEntity(notHostUser)));
	}

	@Test
	@Transactional
	@DisplayName("파티 배달 완료 테스트")
	public void testFinishDelivery() {
		//given
		User hostUser = testUtils.createUser();
		User notHostUser = testUtils.createUser();
		Party party = testUtils.createParty(hostUser, 2L);
		Long partyId = party.getId();

		PartyUpdateRequestDTO requestDTO = PartyUpdateRequestDTO.builder()
			.status(PartyStatus.DELIVERED)
			.build();

		party.startDelivery();

		//when
		partyService.updateParty(partyId, requestDTO, UserDTO.fromEntity(hostUser));

		//then
		assertEquals(PartyStatus.DELIVERED, party.getStatus());
		assertThrows(IllegalStateException.class,
			() -> partyService.updateParty(partyId, requestDTO, UserDTO.fromEntity(notHostUser)));
	}

	@Test
	@Transactional
	@DisplayName("파티 종료 테스트")
	public void testFinishParty() {
		//given
		User hostUser = testUtils.createUser();
		User notHostUser = testUtils.createUser();
		Party party = testUtils.createParty(hostUser, 2L);
		Long partyId = party.getId();

		PartyUpdateRequestDTO requestDTO = PartyUpdateRequestDTO.builder()
			.status(PartyStatus.FINISHED)
			.build();

		party.startDelivery();
		party.finishDelivery();

		//when
		partyService.updateParty(partyId, requestDTO, UserDTO.fromEntity(hostUser));

		//then
		assertEquals(PartyStatus.FINISHED, party.getStatus());
		assertThrows(IllegalStateException.class,
			() -> partyService.updateParty(partyId, requestDTO, UserDTO.fromEntity(notHostUser)));
	}

	@Test
	@Transactional
	@DisplayName("파티 취소 테스트")
	public void testCancelParty() {
		//given
		User hostUser = testUtils.createUser();
		User notHostUser = testUtils.createUser();
		Party party = testUtils.createParty(hostUser, 2L);
		Long partyId = party.getId();
		PartyUpdateRequestDTO requestDTO = PartyUpdateRequestDTO.builder()
			.status(PartyStatus.CANCELED)
			.build();

		//when
		partyService.updateParty(partyId, requestDTO, UserDTO.fromEntity(hostUser));

		//then
		assertEquals(PartyStatus.CANCELED, party.getStatus());
		assertThrows(IllegalStateException.class,
			() -> partyService.updateParty(partyId, requestDTO, UserDTO.fromEntity(notHostUser)));
	}

	@Test
	@Transactional
	@DisplayName("테스트 데이터 생성")
	public void test() throws Exception {
		//given
		Address address = Address.builder()
			.address("서울특별시 동작구 상도로 369")
			.roadAddress("서울특별시 동작구 상도로 369")
			.coordinate(Coordinate.builder()
				.latitude(37.49471)
				.longitude(126.96000)
				.build())
			.zipCode("06978")
			.build();
		Coordinate center = address.getCoordinate();

		IntStream.range(0, 100).forEach(i -> {
			User user = testUtils.getRandomUser();
			Coordinate newCoordinate = center.getVertex(Math.random()).getLeft();
			Address newAddress = Address.builder()
				.address("서울특별시 동작구 상도로 369")
				.roadAddress("서울특별시 동작구 상도로 369")
				.coordinate(newCoordinate)
				.zipCode("06978")
				.build();

			PartyRegisterFormDTO createFormDTO = PartyRegisterFormDTO.builder()
				.title("테스트 파티" + i)
				.description("테스트 파티 description" + i)
				.address(newAddress)
				.limitMemberCount(10L)
				.foodCategory(testUtils.getRandomFoodCategory())
				.partyTime(testUtils.getRandomFutureTime())
				.build();

			partyService.createParty(createFormDTO, UserDTO.fromEntity(user));
		});
	}
}
