package com.seven.marketclip.wishList.repository;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.wishList.domain.WishLists;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishListsRepository extends JpaRepository<WishLists, Long> {
    List<WishLists> findAllByGoodsId(Long goodsId);
    Optional<WishLists> findByGoodsAndAccount(Goods goods, Account account);
}
