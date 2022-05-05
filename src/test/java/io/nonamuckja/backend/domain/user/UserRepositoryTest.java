package io.nonamuckja.backend.domain.user;

import io.bloco.faker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@SuppressWarnings("NonAsciiCharacters")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("유저_저장_테스트")
    public void 유저_저장_테스트() {
        //given
        Faker faker = new Faker();
        String testEmail = faker.internet.email();
        String testUsername = faker.internet.userName();
        String testPassword = faker.internet.password();

        userRepository.save(User.builder().email(testEmail).username(testUsername).password(testPassword).build());

        //when
        User user = userRepository.findByEmail(testEmail).orElseThrow();

        //then
        assertEquals(testEmail, user.getEmail());
        assertEquals(testUsername, user.getUsername());
    }

    @Test
    @DisplayName("findByEmail 테스트")
    public void findByEmail() {
        //given
        Faker faker = new Faker();
        String testEmail = faker.internet.email();
        String testUsername = faker.internet.userName();
        String testPassword = faker.internet.password();

        userRepository.save(User.builder().email(testEmail).username(testUsername).password(testPassword).build());

        //when
        User user = userRepository.findByEmail(testEmail).orElseThrow();

        //then
        assertEquals(testEmail, user.getEmail());
        assertEquals(testUsername, user.getUsername());
    }

    @Test
    @DisplayName("findByUsername 테스트")
    @Transactional
    public void findByUsername() {
        //given
        Faker faker = new Faker();
        String testEmail = faker.internet.email();
        String testUsername = faker.internet.userName();
        String testPassword = faker.internet.password();

        userRepository.save(User.builder().email(testEmail).username(testUsername).password(testPassword).build());

        //when
        User user = userRepository.findByUsername(testUsername).orElseThrow();

        //then
        assertEquals(testEmail, user.getEmail());
        assertEquals(testUsername, user.getUsername());
    }
}