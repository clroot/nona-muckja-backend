package io.nonamuckja.backend.domain.party;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import io.nonamuckja.backend.TestUtils;
import io.nonamuckja.backend.domain.Address;
import io.nonamuckja.backend.domain.user.User;

@SpringBootTest
@Transactional
class PartyTest {
	@Autowired
	private TestUtils testUtils;

	@Test
	@DisplayName("Party 저장 테스트")
	public void save() {
		//given
		User host = testUtils.createUser();
		Address address = testUtils.createAddress();

		//when
		Party party = Party.builder()
			.host(host)
			.address(address)
			.build();

		//then
		assertEquals(host.getId(), party.getHost().getId());
		assertEquals(address.getRoadAddress(), party.getAddress().getRoadAddress());
	}
}
