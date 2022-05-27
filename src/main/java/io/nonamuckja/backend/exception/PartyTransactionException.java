package io.nonamuckja.backend.exception;

import org.springframework.http.HttpStatus;

public class PartyTransactionException extends CustomException {

	public PartyTransactionException(String message, HttpStatus status) {
		super(message, status);
	}
}
