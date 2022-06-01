package io.nonamuckja.backend.web.dto;

import java.util.ArrayList;
import java.util.List;

import io.nonamuckja.backend.domain.Coordinate;
import io.nonamuckja.backend.domain.party.FoodCategory;
import io.nonamuckja.backend.domain.party.PartyStatus;
import io.swagger.v3.oas.annotations.Parameter;
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
public class PartySearchRequestDTO {
	@Parameter(description = "클라이언트의 현재 GPS 좌표")
	private Coordinate clientLocation;

	@Parameter(description = "클라이언트가 찾고자 하는 파티의 위치 반경")
	private Double radius;

	@Builder.Default
	@Parameter(description = "검색하고자 하는 파티 상태")
	private PartyStatus status = PartyStatus.OPEN;

	@Parameter(description = "클라이언트가 선택한 음식 카테고리")
	private List<FoodCategory> foodCategories;
}
