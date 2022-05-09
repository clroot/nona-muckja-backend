package io.nonamuckja.backend.service;

import io.bloco.faker.Faker;
import io.nonamuckja.backend.domain.user.User;
import io.nonamuckja.backend.domain.user.UserRepository;
import io.nonamuckja.backend.dto.UserRegisterFormDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("회원가입 테스트")
    public void register() {
        //given
        UserRegisterFormDTO userRegisterFormDTO = createUserRegisterFormDTO();

        //when
        Long registeredUserId = userService.register(userRegisterFormDTO);

        //then
        User registeredUser = userRepository.findById(registeredUserId).orElseThrow();
        assertEquals(userRegisterFormDTO.getUsername(), registeredUser.getUsername());
        assertEquals(userRegisterFormDTO.getEmail(), registeredUser.getEmail());
        assertNotEquals(userRegisterFormDTO.getPassword(), registeredUser.getPassword());
        assertNotNull(registeredUser.getRoles());
    }

    @Test
    @DisplayName("중복 회원가입 테스트")
    public void registerDuplicate() {
        //given
        UserRegisterFormDTO userRegisterFormDTO = createUserRegisterFormDTO();

        //when
        userService.register(userRegisterFormDTO);

        //then
        assertThrows(IllegalStateException.class,
                () -> userService.register(userRegisterFormDTO));
    }

    private UserRegisterFormDTO createUserRegisterFormDTO() {
        Faker faker = new Faker();
        String username = faker.internet.userName();
        String password = faker.internet.password();
        String email = faker.internet.email();

        return UserRegisterFormDTO.builder().username(username).password(password).email(email).build();
    }
}