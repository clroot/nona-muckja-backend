package io.nonamuckja.backend.web.dto;

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
public class PartyUpdateRequestDTO {
	private PartyStatus status;
}
