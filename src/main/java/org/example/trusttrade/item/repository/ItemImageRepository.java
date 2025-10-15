package org.example.trusttrade.item.repository;

import org.example.trusttrade.item.domain.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemImageRepository extends JpaRepository<ItemImage,Integer> {
    @Query("SELECT i FROM ItemImage i WHERE i.item.id = :itemId ORDER BY i.main_Image DESC, i.id ASC")
    List<ItemImage> findAllByItemIdOrderByMainFirst(@Param("itemId") Long itemId);

}
