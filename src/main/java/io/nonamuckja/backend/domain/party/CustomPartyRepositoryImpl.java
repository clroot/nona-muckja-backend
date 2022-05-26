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

	private JPQLQuery<Party> searchPartyQuery(PartySearch partySearch, Pageable pageable) {
		QParty party = QParty.party;
		JPQLQuery<Party> query = from(party);

		if (partySearch.getFrom() != null && partySearch.getTo() != null) {
			double lat1 = partySearch.getFrom().getLatitude();
			double lon1 = partySearch.getFrom().getLongitude();
			double lat2 = partySearch.getTo().getLatitude();
			double lon2 = partySearch.getTo().getLongitude();

			query.where(
				party.address.coordinate.latitude.between(
					Math.min(lat1, lat2),
					Math.max(lat1, lat2)),
				party.address.coordinate.longitude.between(
					Math.min(lon1, lon2),
					Math.max(lon1, lon2))
			);
		}

		if (partySearch.getStatus() != null) {
			query.where(party.status.eq(partySearch.getStatus()));
		}

		if (pageable != null) {
			query.limit(pageable.getPageSize());
			query.offset(pageable.getOffset());
		}

		return query.orderBy(party.id.desc());
	}
}
