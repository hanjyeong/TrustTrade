package org.example.trusttrade.item.repository;

import org.example.trusttrade.item.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item,Integer> {

}
