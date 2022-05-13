package io.nonamuckja.backend.domain.user;

import io.nonamuckja.backend.domain.Address;
import io.nonamuckja.backend.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(indexes = {@Index(name = "idx_user_email", columnList = "email", unique = true)})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Getter
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String picture;

    @Embedded
    private Address address;

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private Set<UserRole> roles = new HashSet<>();

    public void addRole(UserRole role) {
        roles.add(role);
    }

    public User update(String username,String picture)
    {
        this.username=username;
        this.picture=picture;

        return this;
    }
}