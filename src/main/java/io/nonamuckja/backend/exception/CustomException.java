package io.nonamuckja.backend.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public abstract class CustomException extends RuntimeException {
	private final HttpStatus status;

	public CustomException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}
}
