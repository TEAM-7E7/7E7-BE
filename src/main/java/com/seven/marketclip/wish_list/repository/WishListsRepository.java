package com.seven.marketclip.wish_list.repository;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.wish_list.domain.WishLists;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishListsRepository extends JpaRepository<WishLists, Long> {
    List<WishLists> findAllByGoodsId(Long goodsId);
    Optional<WishLists> findByGoodsAndAccount(Goods goods, Account account);
    Page<WishLists> findAllByAccountIdOrderByCreatedAtDesc(Long accountId, Pageable pageable);
}
