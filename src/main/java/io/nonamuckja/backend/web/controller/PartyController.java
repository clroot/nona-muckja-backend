package io.nonamuckja.backend.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.nonamuckja.backend.domain.party.Party;
import io.nonamuckja.backend.domain.party.PartyRepository;
import io.nonamuckja.backend.security.AuthUserDTO;
import io.nonamuckja.backend.security.CheckLoginUser;
import io.nonamuckja.backend.service.PartyService;
import io.nonamuckja.backend.web.dto.PartyDTO;
import io.nonamuckja.backend.web.dto.PartyRegisterFormDTO;
import io.nonamuckja.backend.web.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/party")
@RequiredArgsConstructor
@Slf4j
public class PartyController {
	private final PartyService partyService;
	private final PartyRepository partyRepository;

	@PostMapping
	@CheckLoginUser
	public ResponseEntity<PartyDTO> registerParty(
		@AuthUserDTO UserDTO userDTO,
		@RequestBody PartyRegisterFormDTO registerFormDTO) {
		log.info("Party registration request received: {}", registerFormDTO);

		Long registeredId = partyService.createParty(registerFormDTO, userDTO);
		Party registeredParty = partyRepository.findById(registeredId).orElseThrow();

		log.info("Party registration successful: {}", registeredParty);

		PartyDTO registeredPartyDTO = PartyDTO.fromEntity(registeredParty);

		return new ResponseEntity<>(registeredPartyDTO, HttpStatus.CREATED);
	}
}
