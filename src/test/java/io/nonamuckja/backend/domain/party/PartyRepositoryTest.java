package io.nonamuckja.backend.domain.party;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import io.nonamuckja.backend.TestUtils;
import io.nonamuckja.backend.domain.Address;
import io.nonamuckja.backend.domain.Coordinate;
import io.nonamuckja.backend.domain.user.User;

@SpringBootTest
@Transactional
class PartyRepositoryTest {
	@Autowired
	PartyRepository partyRepository;

	@Autowired
	TestUtils testUtils;

	Coordinate center;

	@BeforeEach
	void setUp() {
		User host = testUtils.createUser();
		Address partyAddress = testUtils.createAddress();
		center = partyAddress.getCoordinate();

		IntStream.range(0, 100).forEach(i -> {
			Coordinate coordinate;
			if (i < 20) {
				coordinate = center.getVertex(0.19).getLeft();
			} else if (i < 50) {
				coordinate = center.getVertex(0.49).getLeft();
			} else if (i < 80) {
				coordinate = center.getVertex(0.79).getLeft();
			} else {
				coordinate = center.getVertex(0.9).getLeft();
			}
			Address address = Address.builder()
				.address("서울시 강남구 역삼동")
				.roadAddress("서울시 강남구 역삼동")
				.zipCode("12345")
				.coordinate(coordinate)
				.build();

			testUtils.createParty(host, 10L, address);
		});
	}

	@Test
	@DisplayName("search() 테스트")
	public void testSearch() {
		// given
		double radius = 0.2; // 500m
		var vertex = center.getVertex(radius);
		PartySearch search = PartySearch.builder()
			.from(vertex.getLeft())
			.to(vertex.getRight())
			.foodCategories(List.of(FoodCategory.ASIAN_FOOD, FoodCategory.CHINESE))
			.build();

		// when
		List<Party> partyList = partyRepository.search(search);

		// then
		partyList.forEach(party -> {
			assertTrue(center.getDistanceWith(party.getAddress().getCoordinate()) <= radius);
			assertTrue(search.getFoodCategories().contains(party.getFoodCategory()));
		});
	}
}
