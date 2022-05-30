package io.nonamuckja.backend.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends CustomException {
	public UserNotFoundException() {
		super("해당 유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
	}

	public UserNotFoundException(String message) {
		super(message, HttpStatus.NOT_FOUND);
	}
}
