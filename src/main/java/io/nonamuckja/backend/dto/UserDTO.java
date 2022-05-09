package io.nonamuckja.backend.dto;

import io.nonamuckja.backend.domain.Address;
import io.nonamuckja.backend.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserDTO extends org.springframework.security.core.userdetails.User {

    private Long id;

    private String email;

    private Address address;

    private UserDTO(String username, String password,
                    Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public static UserDTO entityToDTO(User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        Collection<? extends GrantedAuthority> authorities =
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).collect(Collectors.toList());

        UserDTO userDTO = new UserDTO(username, password, authorities);

        userDTO.id = user.getId();
        userDTO.email = user.getEmail();
        userDTO.address = user.getAddress();

        return userDTO;
    }
}
