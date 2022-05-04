package io.nonamuckja.backend.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("NonAsciiCharacters")
class UserTest {

    @Test
    @DisplayName("User 생성 테스트")
    public void User_객체는_builder_메서드로_생성한다() {
        //given
        String testEmail = "test@email.com";
        String testName = "test";

        //when
        User user = User.builder().email(testEmail).username(testName).build();

        //then
        assertEquals(testEmail, user.getEmail());
        assertEquals(testName, user.getUsername());
    }
}