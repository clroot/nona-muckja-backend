package io.nonamuckja.backend.domain.party;

import io.nonamuckja.backend.domain.Address;
import io.nonamuckja.backend.domain.BaseTimeEntity;
import io.nonamuckja.backend.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "party")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Getter
public class Party extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_user_id")
    private User host;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PartyUser> members = new ArrayList<>();
}