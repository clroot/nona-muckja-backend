package io.nonamuckja.backend.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {

    private String street;

    private String city;

    private String state;

    private String zip;
}
