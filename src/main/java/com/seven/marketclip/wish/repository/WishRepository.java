package com.seven.marketclip.wish.repository;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.wish.domain.Wish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish, Long> {
    List<Wish> findAllByGoodsId(Long goodsId);

    @Query(value = "select g.account_id from goods as g right join wish_lists as wl on g.id = wl.goods_id where g.id = :goodsId", nativeQuery = true)
    List<Long> findAllAccountIdByGoodsId(Long goodsId);

    Optional<Wish> findByGoodsAndAccount(Goods goods, Account account);
    Page<Wish> findAllByAccountIdOrderByCreatedAtDesc(Long accountId, Pageable pageable);
}
