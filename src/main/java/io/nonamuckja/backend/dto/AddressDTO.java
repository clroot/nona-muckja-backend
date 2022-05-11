package io.nonamuckja.backend.dto;

import io.nonamuckja.backend.domain.Address;
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
public class AddressDTO {
	private String address; // 지번주소

	private String roadAddress; // 도로명주소

	private String zipCode; // 우편번호

	private Double x; // longitude

	private Double y; // latitude

	public Address toEntity() {
		return Address.builder()
			.address(address)
			.roadAddress(roadAddress)
			.zipCode(zipCode)
			.x(x)
			.y(y)
			.build();
	}
}
