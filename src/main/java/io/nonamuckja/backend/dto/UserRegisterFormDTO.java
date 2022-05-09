package io.nonamuckja.backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class UserRegisterFormDTO {

    private String username;

    private String password;

    private String email;
}
