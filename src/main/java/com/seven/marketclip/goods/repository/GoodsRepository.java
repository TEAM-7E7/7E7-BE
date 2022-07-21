package com.seven.marketclip.goods.repository;

import com.seven.marketclip.goods.domain.Goods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.EntityManager;

public interface GoodsRepository extends JpaRepository<Goods, Long> {

//    @Query(value = "SELECT * FROM goods :category :order", nativeQuery = true)
//    Page<Goods> pagingGoods(@Param(value = "category") String categoryQuery, @Param(value = "order") String orderByQuery, Pageable pageable);


//        @Query(value = "SELECT * FROM goods where category = 'LIVING_INSTANCE' order by created_at desc", nativeQuery = true)
    @Query(value = "select * from goods where"+" category = 'LIVING_INSTANCE'", nativeQuery = true)
    Page<Goods> pagingGoods(@Param(value = "category") String categoryQuery, @Param(value = "order") String orderByQuery, Pageable pageable);

//    @Query(value = "SELECT * FROM goods left join wish_lists on goods.id = wish_lists.goods_id :category :order", nativeQuery = true)
//    Page<Goods> pagingGoodsWishList(@Param(value = "category") String categoryQuery, @Param(value = "order") String orderByQuery, Pageable pageable);


    // 내가 쓴 글 보기
    Page<Goods> findAllByAccountIdOrderByCreatedAtDesc(Long accountId, Pageable pageable);

    // 조회수 + 1
    @Modifying
    @Query("UPDATE Goods p SET p.viewCount = p.viewCount + 1 where p.id = :id")
    void updateView(Long id);

    // 즐겨찾기 순으로 정렬
    @Query(value = "SELECT g FROM Goods g left join wish_lists w on g = w.goods group by g.id order by count(w) desc")
    Page<Goods> findAllByOrderByWishListsCount(Pageable pageable);

}
