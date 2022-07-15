package com.seven.marketclip.goods.repository;

import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.domain.GoodsCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface GoodsRepository extends JpaRepository<Goods, Long> {
    Page<Goods> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Goods> findAllByAccountIdOrderByCreatedAtDesc(Long accountId, Pageable pageable);
    Page<Goods> findAllByCategoryOrderByCreatedAtDesc(GoodsCategory category, Pageable pageable);

    @Modifying
    @Query("UPDATE Goods p SET p.viewCount = p.viewCount + 1 where p.id = :id")
    void updateView(Long id);

    @Query(value = "SELECT g FROM Goods g left join WishLists w on g = w.goods group by g.id order by count(w) desc")
    Page<Goods> findAllByOrderByWishListsCount(Pageable pageable);
}
