package io.nonamuckja.backend;

import static java.util.concurrent.TimeUnit.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.github.javafaker.Faker;

import io.nonamuckja.backend.domain.Address;
import io.nonamuckja.backend.domain.Coordinate;
import io.nonamuckja.backend.domain.party.FoodCategory;
import io.nonamuckja.backend.domain.party.Party;
import io.nonamuckja.backend.domain.party.PartyRepository;
import io.nonamuckja.backend.domain.user.User;
import io.nonamuckja.backend.domain.user.UserRepository;
import io.nonamuckja.backend.service.AuthService;
import io.nonamuckja.backend.service.PartyService;
import io.nonamuckja.backend.web.dto.PartyRegisterFormDTO;
import io.nonamuckja.backend.web.dto.UserDTO;
import io.nonamuckja.backend.web.dto.UserRegisterFormDTO;

@Component
@Transactional
public class TestUtils {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PartyRepository partyRepository;

	@Autowired
	private PartyService partyService;

	@Autowired
	private AuthService authService;

	public User createUser() {
		Faker faker = new Faker();

		String email = faker.internet().emailAddress();
		String username = faker.name().username();
		String password = faker.internet().password();

		return createUser(email, username, password);
	}

	public User createUser(UserRegisterFormDTO registerFormDTO) {
		return createUser(registerFormDTO.getEmail(), registerFormDTO.getUsername(), registerFormDTO.getPassword());
	}

	public User createUser(String email, String username, String password) {
		UserRegisterFormDTO userRegisterDTO = UserRegisterFormDTO.builder()
			.email(email)
			.username(username)
			.password(password)
			.address(createAddress())
			.build();

		Long registeredId = authService.register(userRegisterDTO);

		return userRepository.findById(registeredId).orElseThrow();
	}

	public Party createParty(User host, Long limitMemberCount, Address address) {
		Faker faker = new Faker();
		PartyRegisterFormDTO partyRegisterDTO = PartyRegisterFormDTO.builder()
			.address(address)
			.limitMemberCount(limitMemberCount)
			.foodCategory(getRandomFoodCategory())
			.partyTime(getRandomFutureTime())
			.build();
		UserDTO hostDTO = UserDTO.fromEntity(host);

		Long createdPartyId = partyService.createParty(partyRegisterDTO, hostDTO);

		return partyRepository.findById(createdPartyId).orElseThrow();
	}

	public Party createParty(User host, Long limitMemberCount) {
		return createParty(host, limitMemberCount, createAddress());
	}

	public UserRegisterFormDTO createUserRegisterFormDTO() {
		Faker faker = new Faker();

		String email = faker.internet().emailAddress();
		String username = faker.name().username();
		String password = faker.internet().password();

		return UserRegisterFormDTO.builder()
			.username(username)
			.password(password)
			.email(email)
			.address(createAddress())
			.build();
	}

	public Address createAddress() {
		Faker faker = new Faker();
		return Address.builder()
			.address(faker.address().city() + " " + faker.address().secondaryAddress())
			.roadAddress(faker.address().streetAddress())
			.zipCode(faker.address().zipCode())
			.coordinate(createCoordinate())
			.build();

	}

	public Coordinate createCoordinate() {
		Faker faker = new Faker();
		return Coordinate.builder()
			.longitude(Double.parseDouble(faker.address().longitude()))
			.latitude(Double.parseDouble(faker.address().latitude()))
			.build();
	}

	public FoodCategory getRandomFoodCategory() {
		return FoodCategory.values()[(int)(Math.random() * FoodCategory.values().length)];
	}

	public LocalDateTime getRandomFutureTime() {
		Faker faker = new Faker();
		return faker.date()
			.future(7, DAYS)
			.toInstant()
			.atZone(ZoneId.systemDefault())
			.toLocalDateTime();
	}

	public User getRandomUser() {
		User user = userRepository.findAll().get((int)(Math.random() * userRepository.count()));
		if (user == null) {
			return createUser();
		}
		return user;
	}

	public Party getRandomParty() {
		Party party = partyRepository.findAll().get((int)(Math.random() * partyRepository.count()));
		if (party == null) {
			return createParty(getRandomUser(), (long)(Math.random() * 10));
		}
		return party;
	}
}
