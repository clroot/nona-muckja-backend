package io.nonamuckja.backend.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.github.javafaker.Faker;

class CoordinateTest {

	@Test
	@DisplayName("getDistanceWith() 테스트")
	public void testGetDistanceWith() {
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
		Double distance = ssuCoordinate.getDistanceWith(homeCoordinate);

		// then
		assertTrue(Math.abs(distance - distanceByMap) < toleranceInCoordinate);
	}

	@RepeatedTest(1000)
	@DisplayName("getVertex() 테스트")
	public void testGetVertex() {
		//given
		final Coordinate center = createCoordinate();
		final Double radius = 0.5; // 500m
		final Double toleranceInDistance = 0.005;

		//when
		Pair<Coordinate, Coordinate> vertexCoordinates = center.getVertex(radius);

		Coordinate left = vertexCoordinates.getLeft();
		Coordinate right = vertexCoordinates.getRight();

		//then
		double leftDistance = center.getDistanceWith(left);
		double rightDistance = center.getDistanceWith(right);

		boolean isLeftPass =
			radius - toleranceInDistance <= leftDistance && leftDistance <= radius + toleranceInDistance;
		boolean isRightPass =
			radius - toleranceInDistance <= rightDistance && rightDistance <= radius + toleranceInDistance;

		assertTrue(isLeftPass);
		assertTrue(isRightPass);
	}

	private Coordinate createCoordinate() {
		Faker faker = new Faker();
		return Coordinate.builder()
			.longitude(Double.parseDouble(faker.address().longitude()))
			.latitude(Double.parseDouble(faker.address().latitude()))
			.build();
	}
}
