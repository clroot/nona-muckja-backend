package io.nonamuckja.backend;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NonaMuckjaBackendApplicationTests {

	@Test
	void contextLoads() {
	}

	@SelectPackages({
		"io.nonamuckja.backend.domain",
		"io.nonamuckja.backend.service",
		"io.nonamuckja.backend.web",
		"io.nonamuckja.backend.security"})
	@Suite
	public static class AllTestSuite {
	}

	@SelectPackages("io.nonamuckja.backend.domain")
	@Suite
	public static class DomainTestSuite {
	}

	@SelectPackages("io.nonamuckja.backend.security")
	@Suite
	public static class SecurityTestSuite {
	}

	@SelectPackages("io.nonamuckja.backend.service")
	@Suite
	public static class ServiceTestSuite {
	}

	@SelectPackages("io.nonamuckja.backend.web")
	@Suite
	public static class WebTestSuite {
	}
}
