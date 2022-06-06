package io.nonamuckja.backend.domain.party;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomPartyRepository {

	List<Party> search(PartySearch partySearch);

	Page<Party> search(PartySearch partySearch, Pageable pageable);

	List<Party> getPartyByMember(Long userId);

	Page<Party> getPartyByMember(Long userId, Pageable pageable);
}
