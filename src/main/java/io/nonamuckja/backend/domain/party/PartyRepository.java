package io.nonamuckja.backend.domain.party;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyRepository
	extends JpaRepository<Party, Long>, JpaSpecificationExecutor<Party>, CustomPartyRepository {
	Page<Party> findAllByOrderByIdDesc(Pageable pageable);

}
