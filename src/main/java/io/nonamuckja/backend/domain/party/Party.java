package io.nonamuckja.backend.domain.party;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
import io.nonamuckja.backend.exception.PartyTransactionException;
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

	private LocalDateTime partyTime;

	@Enumerated(EnumType.STRING)
	@Builder.Default
	private PartyStatus status = PartyStatus.OPEN;

	@Enumerated(EnumType.STRING)
	private FoodCategory foodCategory;

	@OneToMany(mappedBy = "party", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Builder.Default
	private List<PartyUser> members = new ArrayList<>();

	private String title;

	private String description;

	/*=====BUSINESS METHODS=====*/
	public void joinMember(User member) {
		if (status != PartyStatus.OPEN) {
			throw new PartyTransactionException("참여할 수 없는 파티입니다.");
		} else if (limitMemberCount <= members.size()) {
			throw new PartyTransactionException("파티 참여 인원이 제한수를 초과하였습니다.");
		} else if (isMemberOfParty(member)) {
			throw new PartyTransactionException("이미 참여중인 파티입니다.");
		}

		members.add(PartyUser.builder()
			.party(this)
			.user(member)
			.build());
	}

	public void leaveMember(User member) {
		if (status != PartyStatus.OPEN) {
			throw new PartyTransactionException("주문이 완료되어 떠날 수 없는 파티입니다.");
		} else if (!isMemberOfParty(member)) {
			throw new PartyTransactionException("참여하지 않은 파티입니다.");
		}
		members.removeIf(m -> m.getUser().getId().equals(member.getId()));
	}

	public void startDelivery() {
		if (status != PartyStatus.OPEN) {
			throw new PartyTransactionException("배달 정보를 변경할 수 없는 파티입니다.");
		}
		status = PartyStatus.DELIVERING;
	}

	public void finishDelivery() {
		if (status != PartyStatus.DELIVERING) {
			throw new PartyTransactionException("파티의 음식이 배송 중이 아닙니다.");
		}
		status = PartyStatus.DELIVERED;
	}

	public void finishParty() {
		if (status != PartyStatus.DELIVERED) {
			throw new PartyTransactionException("파티 음식의 배달이 완료되지 않았습니다.");
		}
		status = PartyStatus.FINISHED;
	}
// 카테고리 in (리스트 넣으면) 그중에 해당되는 다 웨어 조건에 걸리게끔
	public void cancelParty() {
		if (status != PartyStatus.OPEN) {
			throw new PartyTransactionException("파티를 취소할 수 없습니다.");
		}
		status = PartyStatus.CANCELED;
	}

	private boolean isMemberOfParty(User user) {
		return members.stream()
			.anyMatch(partyUser -> Objects.equals(partyUser.getUser().getId(), user.getId()));
	}
}
