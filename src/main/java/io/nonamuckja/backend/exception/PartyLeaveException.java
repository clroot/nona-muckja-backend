package io.nonamuckja.backend.exception;

import org.springframework.http.HttpStatus;

public class PartyLeaveException extends CustomException {

	public PartyLeaveException(String message, HttpStatus status) {
		super(message, status);
	}

	public PartyLeaveException(String message) {
		super(message, HttpStatus.BAD_REQUEST);
	}
}
