package io.nonamuckja.backend.domain.party;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyUserRepository extends JpaRepository<PartyUser, Long> {
	void deleteByPartyIdAndUserId(Long partyId, Long userId);

	Optional<PartyUser> findByPartyIdAndUserId(Long partyId, Long userId);
}
