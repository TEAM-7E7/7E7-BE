package com.seven.marketclip.goods.repository;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.domain.GoodsCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GoodsRepository extends JpaRepository<Goods, Long> {
    Page<Goods> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Goods> findAllByAccount(Account account, Pageable pageable);
    Page<Goods> findAllByCategory(GoodsCategory category, Pageable pageable);

    List<Goods> findAllByAccount(Account account);

    @Modifying
    @Query("UPDATE Goods p SET p.viewCount = p.viewCount + 1 where p.id = :id")
    void updateView(Long id);

    @Modifying
    @Query("update Goods p set p.wishCount = p.wishCount + :value where p.id = :id")
    void updateWishCount(Long id, Integer value);

    //@Query(value = "SELECT p FROM Goods p left join WishLists w on p = p.id + order by count(w) desc")
    //List<Goods> findAllByOrderByWishListsIdsCount();
}
