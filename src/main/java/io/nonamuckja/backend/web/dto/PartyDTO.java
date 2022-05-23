package io.nonamuckja.backend.web.dto;

import java.util.List;
import java.util.stream.Collectors;

import io.nonamuckja.backend.domain.party.Party;
import io.nonamuckja.backend.domain.party.PartyStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PartyDTO {
	private Long id;
	private UserDTO host;
	private AddressDTO address;
	private Long limitMemberCount;
	private Long currentMemberCount;
	private PartyStatus status;
	private List<UserDTO> members;

	public static PartyDTO fromEntity(Party party) {
		return PartyDTO.builder()
			.id(party.getId())
			.host(UserDTO.fromEntity(party.getHost()))
			.address(AddressDTO.fromEntity(party.getAddress()))
			.limitMemberCount(party.getLimitMemberCount())
			.currentMemberCount((long)party.getMembers().size())
			.status(party.getStatus())
			.members(party.getMembers().stream()
				.map(partyUser -> UserDTO.fromEntity(partyUser.getUser()))
				.collect(Collectors.toList()))
			.build();
	}

	public Party toEntity() {
		return Party.builder()
			.id(id)
			.host(host.toEntity())
			.address(address.toEntity())
			.limitMemberCount(limitMemberCount)
			.status(status)
			.build();
	}
}
