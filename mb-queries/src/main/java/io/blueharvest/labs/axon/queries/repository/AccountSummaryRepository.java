package io.blueharvest.labs.axon.queries.repository;


import io.blueharvest.labs.axon.common.model.AccountSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountSummaryRepository extends JpaRepository<AccountSummary, Integer>, PagingAndSortingRepository<AccountSummary, Integer> {
}
