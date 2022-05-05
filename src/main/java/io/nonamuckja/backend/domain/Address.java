package io.nonamuckja.backend.domain;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Getter
@ToString(of = {"roadAddress", "zipCode", "x", "y"})
public class Address {

    private String address; // 지번주소

    private String roadAddress; // 도로명주소

    private String zipCode; // 우편번호

    private Double x; // longitude

    private Double y; // latitude
}
