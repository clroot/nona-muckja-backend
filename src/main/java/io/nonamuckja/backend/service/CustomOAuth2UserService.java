package io.nonamuckja.backend.service;

import io.nonamuckja.backend.domain.user.User;
import io.nonamuckja.backend.domain.user.UserRepository;
import io.nonamuckja.backend.dto.OAuthAttributes;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        User user = saveOrUpdate(attributes);

        return new DefaultOAuth2User(user.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).collect(Collectors.toList()), attributes.getAttributes(), attributes.getNameAttributeKey());
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        String email = attributes.getEmail();
        String username = attributes.getName();
        String picture = attributes.getPicture();

        Optional<User> result = userRepository.findByEmail(email);
        User user;

        if (result.isPresent()) {
            user = result.get();
            user.update(username, picture);
        } else {
            user = User.builder().email(email).username(username).picture(picture).build();
            userRepository.save(user);
        }

        return user;
    }
}
