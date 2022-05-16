package io.nonamuckja.backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.nonamuckja.backend.domain.party.Party;
import io.nonamuckja.backend.domain.party.PartyRepository;
import io.nonamuckja.backend.domain.party.PartyStatus;
import io.nonamuckja.backend.domain.user.User;
import io.nonamuckja.backend.exception.PartyJoinException;
import io.nonamuckja.backend.web.dto.AddressDTO;
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
		AddressDTO partyAddress = createFormDTO.getAddress();
		Long limitMemberCount = createFormDTO.getLimitMemberCount();

		Party party = Party.builder()
			.address(partyAddress.toEntity())
			.host(userDTO.toEntity())
			.limitMemberCount(limitMemberCount)
			.status(PartyStatus.OPEN)
			.build();

		party.joinMember(userDTO.toEntity());

		return partyRepository.save(party).getId();
	}

	@Transactional
	public void joinMember(Long partyId, UserDTO userDTO) {
		Party party = partyRepository.findById(partyId)
			.orElseThrow(() -> new PartyJoinException("존재하지 않는 파티입니다.", HttpStatus.NOT_FOUND));
		User user = userDTO.toEntity();

		checkAbleToJoin(party, user);

		party.joinMember(user);
	}

	private void checkAbleToJoin(Party party, User user) {
		if (party.getStatus() != PartyStatus.OPEN) {
			throw new PartyJoinException("참여할 수 없는 파티입니다.", HttpStatus.BAD_REQUEST);
		} else if (party.getLimitMemberCount() <= party.getMembers().size()) {
			throw new PartyJoinException("참여 인원이 초과되었습니다.", HttpStatus.BAD_REQUEST);
		} else if (party.getMembers().stream()
			.map(partyUser -> partyUser.getUser().getId())
			.anyMatch(userId -> userId.equals(user.getId()))) {
			throw new PartyJoinException("이미 참여한 파티입니다.", HttpStatus.BAD_REQUEST);
		}
	}
}
