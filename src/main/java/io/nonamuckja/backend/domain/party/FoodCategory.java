package io.nonamuckja.backend.domain.party;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FoodCategory {
	JAPANESE("일식"),
	CHINESE("중식"),
	CHICKEN("치킨"),
	PORRIDGE("죽"),
	DESSERT("디저트"),
	SCHOOL_FOOD("분식"),
	STEAM_DISH("찜"),
	PIZZA("피자"),
	WESTERN_FOOD("양식"),
	ROAST_FOOD("고기구이"),
	PIG_HOCKS("족발"),
	ASIAN_FOOD("아시안"),
	FASTFOOD("패스트푸드"),
	LUNCH_BOX("도시락")
	;

	private final String name;
}
