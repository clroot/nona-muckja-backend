package io.nonamuckja.backend.exception;

import org.springframework.http.HttpStatus;

public class PartyJoinException extends CustomException {
	public PartyJoinException(String message, HttpStatus status) {
		super(message, status);
	}
}
