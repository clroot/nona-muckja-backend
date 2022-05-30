package io.nonamuckja.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.nonamuckja.backend.domain.Address;
import io.nonamuckja.backend.domain.party.Party;
import io.nonamuckja.backend.domain.party.PartyRepository;
import io.nonamuckja.backend.domain.party.PartySearch;
import io.nonamuckja.backend.domain.party.PartyStatus;
import io.nonamuckja.backend.domain.user.User;
import io.nonamuckja.backend.exception.PartyJoinException;
import io.nonamuckja.backend.exception.PartyLeaveException;
import io.nonamuckja.backend.exception.PartyNotFoundException;
import io.nonamuckja.backend.web.dto.PartyDTO;
import io.nonamuckja.backend.web.dto.PartyRegisterFormDTO;
import io.nonamuckja.backend.web.dto.PartySearchRequestDTO;
import io.nonamuckja.backend.web.dto.PartyUpdateRequestDTO;
import io.nonamuckja.backend.web.dto.UserDTO;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PartyService {
	private final PartyRepository partyRepository;

	public Page<PartyDTO> list(Pageable pageable) {
		var parties = partyRepository.findAll(pageable);

		return new PageImpl<>(
			parties.stream()
				.map(PartyDTO::fromEntity)
				.collect(Collectors.toList()),
			pageable,
			parties.getTotalElements());
	}

	public PartyDTO findById(Long partyId) {
		var party = getPartyEntity(partyId);
		return PartyDTO.fromEntity(party);
	}

	public Page<PartyDTO> search(PartySearchRequestDTO searchRequestDTO, Pageable pageable) {
		var clientCoordinate = searchRequestDTO.getClientLocation();
		var radius = searchRequestDTO.getRadius();

		var vertex = clientCoordinate.getVertex(radius);
		PartySearch partySearch = PartySearch.builder()
			.from(vertex.getLeft())
			.to(vertex.getRight())
			.status(searchRequestDTO.getStatus())
			.build();

		Page<Party> page = partyRepository.search(partySearch, pageable);
		List<PartyDTO> content = page.getContent().stream()
			.map(PartyDTO::fromEntity)
			.collect(Collectors.toList());

		return new PageImpl<>(content, pageable, page.getTotalElements());
	}

	@Transactional
	public Long createParty(PartyRegisterFormDTO createFormDTO, UserDTO userDTO) {
		Address partyAddress = createFormDTO.getAddress();
		Long limitMemberCount = createFormDTO.getLimitMemberCount();
		LocalDateTime partyTime = createFormDTO.getPartyTime();

		Party party = Party.builder()
			.address(partyAddress)
			.host(userDTO.toEntity())
			.limitMemberCount(limitMemberCount)
			.partyTime(partyTime)
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
	public void updateParty(Long partyId, PartyUpdateRequestDTO requestDTO, UserDTO userDTO) {
		Party party = getPartyEntity(partyId);
		User user = userDTO.toEntity();

		checkHostUser(party, user);

		if (requestDTO.getStatus() != null) {
			PartyStatus status = requestDTO.getStatus();
			if (status == PartyStatus.DELIVERING) {
				party.startDelivery();
			} else if (status == PartyStatus.DELIVERED) {
				party.finishDelivery();
			} else if (status == PartyStatus.FINISHED) {
				party.finishParty();
			} else if (status == PartyStatus.CANCELED) {
				party.cancelParty();
			}
		}
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
