package io.nonamuckja.backend.domain.party;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.jpa.JPQLQuery;

public class CustomPartyRepositoryImpl extends QuerydslRepositorySupport implements CustomPartyRepository {

	public CustomPartyRepositoryImpl() {
		super(Party.class);
	}

	@Override
	public List<Party> search(PartySearch partySearch) {
		var query = searchPartyQuery(partySearch, null);
		return query.fetch();
	}

	@Override
	public Page<Party> search(PartySearch partySearch, Pageable pageable) {
		var query = searchPartyQuery(partySearch, pageable);
		var queryResult = query.fetchResults();
		return new PageImpl<>(queryResult.getResults(), pageable, queryResult.getTotal());
	}

	@Override
	public List<Party> getPartyByMember(Long userId) {
		var query = getPartyByMemberQuery(userId, null);
		return query.fetch();
	}

	@Override
	public Page<Party> getPartyByMember(Long userId, Pageable pageable) {
		var query = getPartyByMemberQuery(userId, pageable);
		var queryResult = query.fetchResults();
		return new PageImpl<>(queryResult.getResults(), pageable, queryResult.getTotal());
	}

	private JPQLQuery<Party> searchPartyQuery(PartySearch partySearch, Pageable pageable) {
		QParty party = QParty.party;
		JPQLQuery<Party> query = from(party);

		if (partySearch.getFrom() != null && partySearch.getTo() != null) {
			query.where(
				party.address.coordinate.latitude.between(
					partySearch.getFrom().getLatitude(),
					partySearch.getTo().getLatitude()),
				party.address.coordinate.longitude.between(
					partySearch.getFrom().getLongitude(),
					partySearch.getTo().getLongitude())
			);
		}

		if (partySearch.getStatus() != null) {
			query.where(party.status.eq(partySearch.getStatus()));
		}

		if (pageable != null) {
			query.limit(pageable.getPageSize());
			query.offset(pageable.getOffset());
		}

		if (partySearch.getFoodCategories().size() > 0) {
			query.where(party.foodCategory.in(partySearch.getFoodCategories()));
		}

		return query.orderBy(party.id.desc());
	}

	private JPQLQuery<Party> getPartyByMemberQuery(Long userId, Pageable pageable) {
		QParty party = QParty.party;
		JPQLQuery<Party> query = from(party);

		query.where(party.members.any().user.id.eq(userId));

		if (pageable != null) {
			query.limit(pageable.getPageSize());
			query.offset(pageable.getOffset());
		}

		return query.orderBy(party.id.desc());
	}
}
