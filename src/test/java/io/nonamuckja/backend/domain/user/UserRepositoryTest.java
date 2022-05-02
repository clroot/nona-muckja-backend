package io.nonamuckja.backend.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@SuppressWarnings("NonAsciiCharacters")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("유저_저장_테스트")
    @Transactional
    public void 유저_저장_테스트() {
        //given
        String testEmail = "test@email.com";
        String testName = "test";
        String testPassword = "test-password";

        userRepository.save(User.builder().email(testEmail).username(testName).password(testPassword).build());

        //when
        User user = userRepository.findByEmail(testEmail);

        //then
        assertEquals(testEmail, user.getEmail());
        assertEquals(testName, user.getUsername());
    }
}