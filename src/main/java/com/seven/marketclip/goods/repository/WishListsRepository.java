package com.seven.marketclip.goods.repository;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.domain.WishLists;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishListsRepository extends JpaRepository<WishLists, Long> {
    Optional<WishLists> findByAccount(Account account);
    Optional<WishLists> findByGoodsAndAccount(Goods goods, Account account);
    //boolean existsWishListsByGoodsAndUsername(Goods goods, String username);
}
