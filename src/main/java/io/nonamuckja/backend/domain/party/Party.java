package io.nonamuckja.backend.domain.party;

import io.nonamuckja.backend.domain.BaseTimeEntity;
import io.nonamuckja.backend.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "party")
@NoArgsConstructor
@Getter
public class Party extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_user_id")
    private User host;

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PartyUser> members = new ArrayList<>();
}