package io.nonamuckja.backend.domain.user;

import io.bloco.faker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("NonAsciiCharacters")
class UserTest {

    @Test
    @DisplayName("User 생성 테스트")
    public void User_객체는_builder_메서드로_생성한다() {
        //given
        Faker faker = new Faker();
        String testEmail = faker.internet.email();
        String testUsername = faker.internet.userName();

        //when
        User user = User.builder().email(testEmail).username(testUsername).build();

        //then
        assertEquals(testEmail, user.getEmail());
        assertEquals(testUsername, user.getUsername());
    }

    @Test
    @DisplayName("User Role 추가 테스트")
    public void addRole() {
        //given
        Faker faker = new Faker();
        String testEmail = faker.internet.email();
        String testUsername = faker.internet.userName();
        User user = User.builder().email(testEmail).username(testUsername).build();
        int beforeRoleCount = user.getRoles().size();

        //when
        user.addRole(UserRole.USER);

        //then
        int afterRoleCount = user.getRoles().size();
        assertEquals(beforeRoleCount + 1, afterRoleCount);
    }
}