package io.nonamuckja.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.nonamuckja.backend.dto.ErrorDTO;
import io.nonamuckja.backend.exception.CustomException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorDTO> handleUserDuplicateException(CustomException e) {
		log.error("Exception: {}", e.getMessage());

		ErrorDTO errorDTO = ErrorDTO.builder()
			.message(e.getMessage())
			.status(e.getStatus().value())
			.build();

		return new ResponseEntity<>(errorDTO, e.getStatus());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDTO> handleException(Exception e) {
		log.error("Exception: {}", e.getMessage());

		ErrorDTO errorDTO = ErrorDTO.builder()
			.message(e.getMessage())
			.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
			.build();

		return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
