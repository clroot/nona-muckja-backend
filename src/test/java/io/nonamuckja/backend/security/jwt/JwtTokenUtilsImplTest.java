package io.nonamuckja.backend.security.jwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JwtTokenUtilsImplTest {
	@Autowired
	private JwtTokenUtils jwtTokenUtils;

	@Test
	@DisplayName("nonamuckja.jwt-secret 구성속성 load 테스트")
	public void contextLoads() {
	}
}
