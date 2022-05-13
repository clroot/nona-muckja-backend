package io.nonamuckja.backend.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuthProvider {
	LOCAL, GOOGLE, NAVER, KAKAO
}
