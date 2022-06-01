package io.nonamuckja.backend.service;

import static org.junit.jupiter.api.Assertions.*;

import javax.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.nonamuckja.backend.TestUtils;
import io.nonamuckja.backend.domain.Address;
import io.nonamuckja.backend.domain.user.User;
import io.nonamuckja.backend.domain.user.UserRepository;
import io.nonamuckja.backend.web.dto.ProfileUpdateFormDTO;
import io.nonamuckja.backend.web.dto.UserDTO;

@SpringBootTest
@Transactional
class UserServiceTest {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TestUtils testUtils;

	@Test
	@DisplayName("프로필 업데이트 성공 테스트")
	public void testUpdateProfile()
	{
		// given

		User user = testUtils.createUser();
		Address testaddress = testUtils.createAddress();
		UserDTO userDTO = UserDTO.fromEntity(user);
		ProfileUpdateFormDTO profileupdateformDTO1 = ProfileUpdateFormDTO.builder()
			.address(testaddress)
			.picture("testpicture")
			.build();



		// when
		userService.updateProfile(profileupdateformDTO1,userDTO);


		// then
		assertEquals(user.getPicture(),"testpicture");
		assertEquals(user.getAddress(),testaddress );


	}
}
