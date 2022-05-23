package io.nonamuckja.backend.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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

	@PostMapping("/{id}/join")
	@CheckLoginUser
	public ResponseEntity<?> joinParty(@PathVariable("id") Long partyId, @AuthUserDTO UserDTO userDTO) {
		log.info("Party join request received: partyId={}, userDTO={}", partyId, userDTO);

		partyService.joinMember(partyId, userDTO);

		log.info("Party join successful: partyId={}, userDTO={}", partyId, userDTO);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/{id}/leave")
	@CheckLoginUser
	public ResponseEntity<?> leaveParty(@PathVariable("id") Long partyId, @AuthUserDTO UserDTO userDTO) {
		log.info("Party leave request received: partyId={}, userDTO={}", partyId, userDTO);

		partyService.leaveMember(partyId, userDTO);

		log.info("Party leave successful: partyId={}, userDTO={}", partyId, userDTO);

		return new ResponseEntity<>(HttpStatus.OK);
	}
}
