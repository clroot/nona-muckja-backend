package io.nonamuckja.backend.domain.party;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import io.nonamuckja.backend.domain.Address;
import io.nonamuckja.backend.domain.BaseTimeEntity;
import io.nonamuckja.backend.domain.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "party", indexes = {@Index(name = "idx_party_host", columnList = "host_user_id")})
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

	private Long limitMemberCount;

	@Enumerated(EnumType.STRING)
	@Builder.Default
	private PartyStatus status = PartyStatus.OPEN;

	@OneToMany(mappedBy = "party", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Builder.Default
	private List<PartyUser> members = new ArrayList<>();

	public void joinMember(User member) {
		members.add(PartyUser.builder()
			.party(this)
			.user(member)
			.build());
	}
}
