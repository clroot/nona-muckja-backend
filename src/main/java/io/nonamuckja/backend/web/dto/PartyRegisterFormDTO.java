package io.nonamuckja.backend.web.dto;

import java.time.LocalDateTime;

import org.hibernate.validator.constraints.Range;

import io.nonamuckja.backend.domain.Address;
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
public class PartyRegisterFormDTO {

	private Address address;
	@Range(min = 1, max = 10)
	private Long limitMemberCount;
	private LocalDateTime partyTime;
}
