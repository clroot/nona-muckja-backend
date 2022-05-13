package io.nonamuckja.backend.exception;

import org.springframework.http.HttpStatus;

public class UserDuplicateException extends CustomException {
	public UserDuplicateException(String message, HttpStatus status) {
		super(message, status);
	}
}
