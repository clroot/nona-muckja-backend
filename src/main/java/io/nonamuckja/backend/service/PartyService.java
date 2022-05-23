package io.nonamuckja.backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.nonamuckja.backend.domain.Address;
import io.nonamuckja.backend.domain.party.Party;
import io.nonamuckja.backend.domain.party.PartyRepository;
import io.nonamuckja.backend.domain.party.PartyStatus;
import io.nonamuckja.backend.domain.user.User;
import io.nonamuckja.backend.exception.PartyJoinException;
import io.nonamuckja.backend.exception.PartyLeaveException;
import io.nonamuckja.backend.exception.PartyNotFoundException;
import io.nonamuckja.backend.web.dto.PartyRegisterFormDTO;
import io.nonamuckja.backend.web.dto.UserDTO;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PartyService {
	private final PartyRepository partyRepository;

	@Transactional
	public Long createParty(PartyRegisterFormDTO createFormDTO, UserDTO userDTO) {
		Address partyAddress = createFormDTO.getAddress();
		Long limitMemberCount = createFormDTO.getLimitMemberCount();

		Party party = Party.builder()
			.address(partyAddress)
			.host(userDTO.toEntity())
			.limitMemberCount(limitMemberCount)
			.status(PartyStatus.OPEN)
			.build();

		party.joinMember(userDTO.toEntity());

		return partyRepository.save(party).getId();
	}

	@Transactional
	public void joinMember(Long partyId, UserDTO userDTO) {
		Party party = getPartyEntity(partyId);
		User user = userDTO.toEntity();

		checkAbleToJoin(party, user);

		party.joinMember(user);
	}

	@Transactional
	public void leaveMember(Long partyId, UserDTO userDTO) {
		Party party = getPartyEntity(partyId);
		User user = userDTO.toEntity();

		checkAbleToLeave(party, user);

		party.leaveMember(user);
	}

	@Transactional
	public void startDelivery(Long partyId, UserDTO userDTO) {
		Party party = getPartyEntity(partyId);
		User user = userDTO.toEntity();

		checkHostUser(party, user);

		party.startDelivery();
	}

	@Transactional
	public void finishDelivery(Long partyId, UserDTO userDTO) {
		Party party = getPartyEntity(partyId);
		User user = userDTO.toEntity();

		checkHostUser(party, user);

		party.finishDelivery();
	}

	@Transactional
	public void finishParty(Long partyId, UserDTO userDTO) {
		Party party = getPartyEntity(partyId);
		User user = userDTO.toEntity();

		checkHostUser(party, user);

		party.finishParty();
	}

	@Transactional
	public void cancelParty(Long partyId, UserDTO userDTO) {
		Party party = getPartyEntity(partyId);
		User user = userDTO.toEntity();

		checkHostUser(party, user);

		party.cancelParty();
	}

	private Party getPartyEntity(Long partyId) {
		return partyRepository.findById(partyId)
			.orElseThrow(PartyNotFoundException::new);
	}

	private void checkAbleToJoin(Party party, User user) {
		if (party.getStatus() != PartyStatus.OPEN) {
			throw new PartyJoinException("참여할 수 없는 파티입니다.", HttpStatus.BAD_REQUEST);
		} else if (party.getLimitMemberCount() <= party.getMembers().size()) {
			throw new PartyJoinException("인원이 제한수를 초과하였습니다.", HttpStatus.BAD_REQUEST);
		} else if (party.getMembers().stream()
			.map(partyUser -> partyUser.getUser().getId())
			.anyMatch(userId -> userId.equals(user.getId()))) {
			throw new PartyJoinException("이미 참여한 파티입니다.", HttpStatus.BAD_REQUEST);
		}
	}

	private void checkAbleToLeave(Party party, User user) {
		if (party.getStatus() != PartyStatus.OPEN) {
			throw new PartyLeaveException("주문이 완료되어 떠날 수 없는 파티입니다.", HttpStatus.BAD_REQUEST);
		} else if (party.getMembers().stream()
			.map(partyUser -> partyUser.getUser().getId())
			.noneMatch(userId -> userId.equals(user.getId()))) {
			throw new PartyLeaveException("참여하지 않은 파티입니다.", HttpStatus.BAD_REQUEST);
		}
	}

	private void checkHostUser(Party party, User user) {
		if (!party.getHost().getId().equals(user.getId())) {
			throw new IllegalStateException("파티의 호스트가 아닙니다.");
		}
	}
}
