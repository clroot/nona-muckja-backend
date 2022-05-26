package io.nonamuckja.backend.service;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import io.nonamuckja.backend.domain.Coordinate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressService {
	/**
	 * 특정 (위도, 경도) 좌표를 기준으로 어느 거리만큼 떨어진 좌표를 반환한다. - 허용 오차: 5m
	 *
	 * @param center 중심점 (위도, 경도) 좌표
	 * @param radius 중심점으로부터 얼마나 떨어져 있는지 (KM 단위)
	 * @return 좌상단과 우하단 좌표 반환
	 */
	public Pair<Coordinate, Coordinate> getVertexCoordinates(Coordinate center, Double radius) {
		return center.getVertex(radius);
	}

	/**
	 * 두 좌표 간 거리를 KM 단위로 계산, 미터 단위 오차까지는 허용됨
	 *
	 * @param p1 첫번째 (위도, 경도) 좌표
	 * @param p2 첫번째 (위도, 경도) 좌표
	 * @return 두 좌표 사이의 거리를 KM 단위로 반환
	 */
	Double getDistanceAsKm(Coordinate p1, Coordinate p2) {
		return p1.getDistanceWith(p2);
	}
}
