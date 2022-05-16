package io.nonamuckja.backend.exception;

import org.springframework.http.HttpStatus;

public class PartyNotFoundException extends CustomException {
	public PartyNotFoundException() {
		super("해당 파티를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
	}

	public PartyNotFoundException(String message) {
		super(message, HttpStatus.NOT_FOUND);
	}
}
