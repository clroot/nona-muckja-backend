package io.nonamuckja.backend.domain.party;

import io.bloco.faker.Faker;
import io.nonamuckja.backend.domain.Address;
import io.nonamuckja.backend.domain.user.User;
import io.nonamuckja.backend.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class PartyTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PartyRepository partyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Party 저장 테스트")
    public void save() {
        //given
        User host = createUser();
        Address address = createAddress();

        System.out.println(address);
        System.out.println(host.getAddress());

        //when
        Party party = Party.builder()
                .host(host)
                .address(address)
                .build();
        partyRepository.save(party);

        //then
        assertEquals(host.getId(), party.getHost().getId());
        assertEquals(address.getRoadAddress(), party.getAddress().getRoadAddress());
    }

    private User createUser() {
        Faker faker = new Faker();

        User user = User.builder()
                .email(faker.internet.email())
                .username(faker.internet.userName())
                .password(passwordEncoder.encode(faker.internet.password()))
                .address(createAddress())
                .build();

        userRepository.save(user);

        return user;
    }

    private Address createAddress() {
        Faker faker = new Faker();
        return Address.builder()
                .address(faker.address.city() + " " + faker.address.secondaryAddress())
                .roadAddress(faker.address.streetAddress())
                .zipCode(faker.address.postcode())
                .x(Double.parseDouble(faker.number.decimal(2, 14)))
                .y(Double.parseDouble(faker.number.decimal(3, 14)))
                .build();
    }
}