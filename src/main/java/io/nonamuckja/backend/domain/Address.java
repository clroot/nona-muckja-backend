package io.nonamuckja.backend.domain;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

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
@ToString(of = {"roadAddress", "zipCode"})
public class Address {

	private String address; // 지번주소

	private String roadAddress; // 도로명주소

	private String zipCode; // 우편번호

	@Embedded
	private Coordinate coordinate; // 좌표
}
