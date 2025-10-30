package org.example.trusttrade.auction.repository;

import org.example.trusttrade.auction.domain.DepositOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepositOrderRepository extends JpaRepository<DepositOrder, String> {
}
