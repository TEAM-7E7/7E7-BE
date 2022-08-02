package com.seven.marketclip.goods.repository;

import com.seven.marketclip.goods.domain.Goods;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface GoodsRepository extends JpaRepository<Goods, Long> {

    // 내가 쓴 글 보기
//    @Query(value = "SELECT * FROM goods where account_id = :accountId and status = :goodsStatus order by created_at desc", nativeQuery = true)
//    PageImpl<Goods> findAllByAccountIdOrderByCreatedAtDesc(Long accountId, String goodsStatus, Pageable pageable);

    // 내가 구매한 글 보기
    @Query(value = "SELECT * FROM goods inner join goods_review on goods.id = goods_review.goods_id where goods_review.account_id = :accountId and goods.status = 'SOLD_OUT' order by goods.created_at desc", nativeQuery = true)
    PageImpl<Goods> findAllPurchaseByAccountIdOrderByCreatedAtDesc(Long accountId, Pageable pageable);

    // 조회수 + 1
    @Modifying
    @Query("UPDATE Goods p SET p.viewCount = p.viewCount + 1 where p.id = :id")
    void updateView(Long id);

}
