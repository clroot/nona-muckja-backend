package io.nonamuckja.backend.service;

import java.util.function.LongPredicate;
import java.util.stream.LongStream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import io.nonamuckja.backend.domain.Coordinate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressService {
	private final double scale = 1000000.0;

	/**
	 * 특정 (위도, 경도) 좌표를 기준으로 어느 거리만큼 떨어진 좌표를 반환한다. - 허용 오차: 5m
	 *
	 * @param center 중심점 (위도, 경도) 좌표
	 * @param radius 중심점으로부터 얼마나 떨어져 있는지 (KM 단위)
	 * @return 좌상단과 우하단 좌표 반환
	 */
	public Pair<Coordinate, Coordinate> getVertexCoordinates(Coordinate center, Double radius) {
		final double bias = calculateBias(center);
		final double radiusScale = 0.1;
		final long scaledLat = (long)(center.getLatitude() * scale);

		LongPredicate checkDistance = x -> {
			long scaledY = calculateY(x, bias);

			double latitude = x / scale;
			double longitude = scaledY / scale;

			Coordinate candidate = Coordinate.builder()
				.latitude(latitude)
				.longitude(longitude)
				.build();

			return getDistanceAsKm(candidate, center) >= radius;
		};

		final long step = 10;
		long left;
		long right;

		// calculate the coordinates of the top-left
		left = (long)(scaledLat - radius * radiusScale * scale);
		right = scaledLat;

		final long finalRight = right;
		final long finalLeft = left;
		long topLeftX = LongStream.range(left, right)
			.map(i -> finalRight - i + finalLeft - 1)
			.filter(i -> i % step == 0)
			.filter(checkDistance)
			.findFirst()
			.orElseThrow();

		Coordinate topLeft = Coordinate.builder()
			.latitude(topLeftX / scale)
			.longitude(calculateY(topLeftX, bias) / scale)
			.build();

		// calculate the coordinates of the bottom-right
		left = scaledLat;
		right = (long)(scaledLat + radius * radiusScale * scale);

		long bottomRightX = LongStream.range(left, right)
			.filter(i -> i % step == 0)
			.filter(checkDistance)
			.findFirst()
			.orElseThrow();

		Coordinate bottomRight = Coordinate.builder()
			.latitude(bottomRightX / scale)
			.longitude(calculateY(bottomRightX, bias) / scale)
			.build();

		return new ImmutablePair<>(topLeft, bottomRight);
	}

	/**
	 * 두 좌표 간 거리를 KM 단위로 계산, 미터 단위 오차까지는 허용됨
	 *
	 * @param p1 첫번째 (위도, 경도) 좌표
	 * @param p2 첫번째 (위도, 경도) 좌표
	 * @return 두 좌표 사이의 거리를 KM 단위로 반환
	 */
	Double getDistanceAsKm(Coordinate p1, Coordinate p2) {
		final int R = 6371; // Radius of the earth

		var lat1 = p1.getLatitude();
		var lon1 = p1.getLongitude();
		var lat2 = p2.getLatitude();
		var lon2 = p2.getLongitude();

		double latDistance = toRadian(lat2 - lat1);
		double lonDistance = toRadian(lon2 - lon1);

		double x1 = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
			+ Math.cos(toRadian(lat1)) * Math.cos(toRadian(lat2))
			* Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

		double x2 = 2 * Math.atan2(Math.sqrt(x1), Math.sqrt(1 - x1));

		return R * x2;
	}

	/**
	 * 특정 좌표로부터 인접한 GPS 좌표를 계산하기 위해, 기울기가 -1인 직선의 방정식의 y 절편을 계산
	 *
	 * @param coordinate 좌표(위도, 경도)
	 * @return 기울기가 -1인 직선의 방정식의 y 절편
	 */
	Double calculateBias(Coordinate coordinate) {
		double bias;

		var latitude = coordinate.getLatitude();
		var longitude = coordinate.getLongitude();

		bias = longitude + latitude;

		return bias;
	}

	/**
	 * x 좌표에 대한 y 좌표를 계산
	 *
	 * @param scaledX x 좌표 * scale
	 * @param bias    y 절편
	 * @return y 좌표 * scale
	 */
	private long calculateY(long scaledX, Double bias) {
		return (long)((-(double)scaledX / scale + bias) * scale);
	}

	/**
	 * 라디안 값을 구함
	 *
	 * @param value 라디안 단위로 변환할 값
	 * @return 라디안 값
	 */
	private Double toRadian(Double value) {
		return value * Math.PI / 180;
	}
}
