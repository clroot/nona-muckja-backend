package io.nonamuckja.backend.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.nonamuckja.backend.domain.user.User;
import io.nonamuckja.backend.domain.user.UserRepository;
import io.nonamuckja.backend.domain.user.UserRole;
import io.nonamuckja.backend.exception.AuthException;
import io.nonamuckja.backend.exception.UserDuplicateException;
import io.nonamuckja.backend.security.jwt.JwtTokenUtils;
import io.nonamuckja.backend.web.dto.AddressDTO;
import io.nonamuckja.backend.web.dto.TokenResponseDTO;
import io.nonamuckja.backend.web.dto.UserLoginFormDTO;
import io.nonamuckja.backend.web.dto.UserRegisterFormDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AuthService {
	private final AuthenticationManager authenticationManager;
	private final UserDetailsService userDetailsService;
	private final JwtTokenUtils jwtTokenUtils;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public TokenResponseDTO login(UserLoginFormDTO loginFormDTO) {
		final String username = loginFormDTO.getUsername();
		final String password = loginFormDTO.getPassword();

		try {
			Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(username, password)
			);
			if (auth.isAuthenticated()) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				String accessToken = jwtTokenUtils.generateToken(userDetails);
				return TokenResponseDTO.builder()
					.accessToken(accessToken)
					.build();
			} else {
				throw new BadCredentialsException("Invalid username or password");
			}
		} catch (BadCredentialsException e) {
			throw new AuthException("잘못된 로그인 정보입니다.", HttpStatus.BAD_REQUEST);
		} catch (DisabledException e) {
			throw new AuthException("비활성화 된 유저입니다.", HttpStatus.FORBIDDEN);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AuthException("알 수 없는 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Transactional
	public Long register(UserRegisterFormDTO userRegisterFormDTO) {
		validateDuplicateUser(userRegisterFormDTO);

		String username = userRegisterFormDTO.getUsername();
		String password = passwordEncoder.encode(userRegisterFormDTO.getPassword());
		String email = userRegisterFormDTO.getEmail();
		AddressDTO address = userRegisterFormDTO.getAddress();

		User user = User.builder()
			.username(username)
			.password(password)
			.email(email)
			.address(address.toEntity())
			.build();

		user.addRole(UserRole.USER);

		userRepository.save(user);

		return user.getId();
	}

	private void validateDuplicateUser(UserRegisterFormDTO userRegisterFormDTO) {
		Optional<User> result = userRepository.findByUsername(userRegisterFormDTO.getUsername());
		if (result.isPresent()) {
			throw new UserDuplicateException("이미 존재하는 사용자입니다.", HttpStatus.CONFLICT);
		}
	}
}
