package io.nonamuckja.backend.web.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
import io.nonamuckja.backend.web.dto.PartySearchRequestDTO;
import io.nonamuckja.backend.web.dto.PartyUpdateRequestDTO;
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

	@GetMapping
	public ResponseEntity<Page<PartyDTO>> list(@PageableDefault Pageable pageable) {
		Page<PartyDTO> parties = partyService.list(pageable);
		return new ResponseEntity<>(parties, HttpStatus.OK);
	}

	@PostMapping("/search")
	@CheckLoginUser
	public ResponseEntity<Page<PartyDTO>> search(
		@RequestBody PartySearchRequestDTO searchRequestDTO,
		@PageableDefault Pageable pageable) {
		log.info("Party Search Request received: {}, pageable: {}", searchRequestDTO, pageable);

		Page<PartyDTO> partyPage = partyService.search(searchRequestDTO, pageable);

		log.info("Party Search Response: {}", partyPage);

		return ResponseEntity.ok(partyPage);
	}

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

	@PatchMapping("/{id}")
	@CheckLoginUser
	public ResponseEntity<?> updateParty(
		@PathVariable("id") Long partyId,
		@RequestBody PartyUpdateRequestDTO requestDTO,
		@AuthUserDTO UserDTO userDTO) {
		log.info("Party update request received: partyId={}, requestDTO={}, userDTO={}", partyId, requestDTO, userDTO);

		partyService.updateParty(partyId, requestDTO, userDTO);

		log.info("Party update successful: partyId={}, requestDTO={}, userDTO={}", partyId, requestDTO, userDTO);

		return new ResponseEntity<>(HttpStatus.OK);
	}
}
