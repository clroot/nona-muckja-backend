package io.nonamuckja.backend.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.nonamuckja.backend.exception.CustomException;
import io.nonamuckja.backend.web.dto.ErrorDTO;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorDTO> handleUserDuplicateException(CustomException exception) {
		log.error("Exception: {}", exception.getMessage());

		ErrorDTO errorDTO = ErrorDTO.builder()
			.message(exception.getMessage())
			.status(exception.getStatus().value())
			.build();

		return new ResponseEntity<>(errorDTO, exception.getStatus());
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ErrorDTO> handleIllegalStateException(IllegalStateException exception) {
		log.error("Exception: {}", exception.getMessage());

		ErrorDTO errorDTO = ErrorDTO.builder()
			.message(exception.getMessage())
			.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
			.build();

		return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDTO> handleException(Exception exception) {
		log.error("Exception: {}", exception.getMessage());

		ErrorDTO errorDTO = ErrorDTO.builder()
			.message(exception.getMessage())
			.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
			.build();

		return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
