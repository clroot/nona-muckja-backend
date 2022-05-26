package io.nonamuckja.backend.domain;

import java.util.function.LongPredicate;
import java.util.stream.LongStream;

import javax.persistence.Embeddable;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Getter
@ToString(includeFieldNames = false)
public class Coordinate {
	/**
	 * The scale of the coordinate to calculate meter unit.
	 */
	private static final double SCALE = 1000000.0;
	/**
	 * The gradient of the coordinate's vertex box.
	 */
	private static final Double VERTEX_GRADIENT = 1.0;
	/**
	 * The tolerance for the distance between two coordinates.(1m)
	 */
	private static final Double DISTANCE_TOLERANCE = 0.001;
	/**
	 * 경도: x
	 */
	private Double latitude;
	/**
	 * 위도: y
	 */
	private Double longitude;

	/*=====BUSINESS METHODS=====*/

	/**
	 * 현재 좌표와 주어진 좌표의 거리를 계산한다(단위: km).
	 * haversine formula 을 이용.
	 *
	 * @param other 좌표
	 * @return 거리(km)
	 */
	public Double getDistanceWith(Coordinate other) {
		final int R = 6371; // Radius of the earth

		double deltaLatitude = Math.toRadians(other.getLatitude() - this.getLatitude());
		double deltaLongitude = Math.toRadians(other.getLongitude() - this.getLongitude());

		double innerFormula = Math.pow(Math.sin(deltaLatitude / 2), 2)
			+ Math.cos(Math.toRadians(this.getLatitude()))
			* Math.cos(Math.toRadians(other.getLatitude()))
			* Math.pow(Math.sin(deltaLongitude / 2), 2);

		return 2 * R * Math.asin(Math.sqrt(innerFormula));
	}

	/**
	 * 현재 좌표를 기준으로 radius(km) 반경의 좌표를 반환한다.
	 * 허용오차: 5m
	 *
	 * @param radius 중심점으로부터 얼마나 떨어져 있는지(km)
	 * @return 좌상단과 우하단 좌표 반환
	 */
	public Pair<Coordinate, Coordinate> getVertex(Double radius) {
		final double bias = calculateBias();
		final double radiusScale = 0.1;
		final long searchStep = 10;
		final long scaledLat = (long)(latitude * SCALE);

		LongPredicate checkDistance = x -> {
			long scaledLon = calculateY(x, bias);

			double lat = x / SCALE;
			double lon = scaledLon / SCALE;

			Coordinate candidate = Coordinate.builder()
				.latitude(lat)
				.longitude(lon)
				.build();

			double distance = this.getDistanceWith(candidate);

			return radius - DISTANCE_TOLERANCE <= distance && distance <= radius + DISTANCE_TOLERANCE;
		};

		long left;
		long right;

		// calculate the coordinate of the left vertex
		left = (long)(scaledLat - radius * radiusScale * SCALE);
		right = scaledLat;

		long leftVertexX = LongStream.rangeClosed(left, right)
			.filter(i -> i % searchStep == 0)
			.filter(checkDistance)
			.findAny()
			.orElseThrow();

		Coordinate leftVertex = Coordinate.builder()
			.latitude(leftVertexX / SCALE)
			.longitude(calculateY(leftVertexX, bias) / SCALE)
			.build();

		// calculate the coordinate of the right vertex
		left = scaledLat;
		right = (long)(scaledLat + radius * radiusScale * SCALE);

		long rightVertexX = LongStream.rangeClosed(left, right)
			.filter(i -> i % searchStep == 0)
			.filter(checkDistance)
			.findAny()
			.orElseThrow();

		Coordinate rightVertex = Coordinate.builder()
			.latitude(rightVertexX / SCALE)
			.longitude(calculateY(rightVertexX, bias) / SCALE)
			.build();

		return new ImmutablePair<>(leftVertex, rightVertex);
	}

	/**
	 * 현재 좌표로부터 인접한 GPS 좌표를 계산하기 위해, 현재 좌표를 지나며 기울기가 GRADIENT 값인 직선의 방정식의 y 절편을 계산
	 *
	 * @return 현재 좌표를 지나며 기울기가 GRADIENT 값인 직선의 방정식의 y 절편
	 */
	private double calculateBias() {
		return longitude - VERTEX_GRADIENT * latitude;
	}

	/**
	 * x 좌표에 대한 y 좌표를 계산
	 *
	 * @param scaledX x 좌표 * scale
	 * @param bias    y 절편
	 * @return y 좌표 * scale
	 */
	private long calculateY(long scaledX, double bias) {
		return (long)((VERTEX_GRADIENT * scaledX / SCALE + bias) * SCALE);
	}
}
