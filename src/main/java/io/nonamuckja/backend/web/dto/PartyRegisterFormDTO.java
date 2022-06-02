package io.nonamuckja.backend.web.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import io.nonamuckja.backend.domain.Address;
import io.nonamuckja.backend.domain.party.FoodCategory;
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
	@NotBlank
	private String title;

	@NotBlank
	private String description;

	@NotBlank
	private Address address;

	@Range(min = 1, max = 10)
	private Long limitMemberCount;

	@NotNull
	private LocalDateTime partyTime;

	@NotNull
	private FoodCategory foodCategory;
}
