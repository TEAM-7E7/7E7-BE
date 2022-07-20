package com.seven.marketclip.goods.repository;

import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.dto.OrderByDTO;
import com.seven.marketclip.goods.enums.GoodsCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GoodsRepository extends JpaRepository<Goods, Long> {

    @Query(value = "SELECT g FROM Goods g left join WishLists w on g = w.goods group by g.id order by count(w) desc")
    Page<Goods> pagingGoods(List<GoodsCategory> categoryList, Pageable pageable);

    // 시간 순서대로
//    @Query(value = "SELECT g FROM Goods g where g.category = :categoryQuery order by g.createdAt desc")
    @Query(value = "SELECT * FROM goods where category = 'LIVING_INSTANCE' order by created_at desc", nativeQuery = true)
    Page<Goods> findAllByOrderByCreatedAtDesc(String categoryQuery, String orderByQuery, Pageable pageable);

    Page<Goods> findAllByOrderByViewCountDesc(Pageable pageable);
    Page<Goods> findAllByCategoryOrderByCreatedAtDesc(GoodsCategory category, Pageable pageable);

    // 내가 쓴 글 보기
    Page<Goods> findAllByAccountIdOrderByCreatedAtDesc(Long accountId, Pageable pageable);

    @Modifying
    @Query("UPDATE Goods p SET p.viewCount = p.viewCount + 1 where p.id = :id")
    void updateView(Long id);

    @Query(value = "SELECT g FROM Goods g left join WishLists w on g = w.goods group by g.id order by count(w) desc")
    Page<Goods> findAllByOrderByWishListsCount(Pageable pageable);

}
