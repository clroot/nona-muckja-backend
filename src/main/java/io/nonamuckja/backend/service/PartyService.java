package io.nonamuckja.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.nonamuckja.backend.domain.party.Party;
import io.nonamuckja.backend.domain.party.PartyRepository;
import io.nonamuckja.backend.domain.party.PartyStatus;
import io.nonamuckja.backend.web.dto.AddressDTO;
import io.nonamuckja.backend.web.dto.PartyCreateFormDTO;
import io.nonamuckja.backend.web.dto.UserDTO;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PartyService {
	private final PartyRepository partyRepository;

	@Transactional
	public Long createParty(PartyCreateFormDTO createFormDTO, UserDTO userDTO) {
		AddressDTO partyAddress = createFormDTO.getAddress();
		Long maxMemberLimit = createFormDTO.getMaxMemberLimit();

		Party party = Party.builder()
			.address(partyAddress.toEntity())
			.host(userDTO.toEntity())
			.maxMemberLimit(maxMemberLimit)
			.status(PartyStatus.OPEN)
			.build();

		return partyRepository.save(party).getId();
	}
}
