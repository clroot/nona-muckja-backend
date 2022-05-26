package io.nonamuckja.backend.domain;

import javax.persistence.Embeddable;

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
	 * 경도: x
	 */
	private Double latitude;
	/**
	 * 위도: y
	 */
	private Double longitude;
}
