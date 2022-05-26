package io.nonamuckja.backend.service;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.nonamuckja.backend.TestUtils;
import io.nonamuckja.backend.domain.Coordinate;

@SpringBootTest
class AddressServiceTest {
	@Autowired
	private AddressService addressService;
	@Autowired
	private TestUtils testUtils;

	@RepeatedTest(100)
	@DisplayName("getVertexCoordinates() 테스트")
	public void testGetVertexCoordinates() {
		//given
		final Coordinate center = testUtils.createAddress().getCoordinate();
		final Double radius = 0.5; // 500m
		final Double toleranceInDistance = 0.005;

		//when
		Pair<Coordinate, Coordinate> vertexCoordinates = addressService.getVertexCoordinates(center, radius);

		Coordinate left = vertexCoordinates.getLeft();
		Coordinate right = vertexCoordinates.getRight();

		//then
		double leftDistance = addressService.getDistanceAsKm(left, center);
		double rightDistance = addressService.getDistanceAsKm(right, center);

		boolean isLeftPass =
			radius - toleranceInDistance <= leftDistance && leftDistance <= radius + toleranceInDistance;
		boolean isRightPass =
			radius - toleranceInDistance <= rightDistance && rightDistance <= radius + toleranceInDistance;

		System.out.println("center: " + center);
		System.out.println("left: " + left);
		System.out.println("right: " + right);
		System.out.println("isLeftPass: " + isLeftPass);
		System.out.println("isRightPass: " + isRightPass);

		assertTrue(isLeftPass);
		assertTrue(isRightPass);
	}

	@Test
	@DisplayName("getDistanceAsKM() 테스트")
	public void testGetDistanceAsMeter() {
		//given
		final double toleranceInCoordinate = 0.00001;
		final double distanceByMap = 4.949862042019746;
		var ssuCoordinate = Coordinate.builder()
			.latitude(37.4963462)
			.longitude(126.9568865)
			.build();
		var homeCoordinate = Coordinate.builder()
			.latitude(37.477248)
			.longitude(126.9062117)
			.build();

		// when
		Double distance = addressService.getDistanceAsKm(ssuCoordinate, homeCoordinate);

		// then
		System.out.println(Math.abs(distance - distanceByMap));
		assertTrue(Math.abs(distance - distanceByMap) < toleranceInCoordinate);
	}
}
