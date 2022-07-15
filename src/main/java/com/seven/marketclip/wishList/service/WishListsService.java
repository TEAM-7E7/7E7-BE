package com.seven.marketclip.wishList.service;


import com.seven.marketclip.account.Account;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.exception.ResponseCode;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.repository.GoodsRepository;
import com.seven.marketclip.security.UserDetailsImpl;
import com.seven.marketclip.wishList.domain.WishLists;
import com.seven.marketclip.wishList.repository.WishListsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.seven.marketclip.exception.ResponseCode.*;

@Service
@RequiredArgsConstructor
public class WishListsService {
    private final GoodsRepository goodsRepository;
    private final WishListsRepository wishListsRepository;

    @Transactional
    public ResponseCode doWishList(Long goodsId, UserDetailsImpl account, String httpMethod) throws CustomException {
        Account detailsAccount = new Account(account);
        Goods goods = goodsRepository.findById(goodsId).orElseThrow(
                () -> new CustomException(GOODS_NOT_FOUND)
        );
        WishLists wishLists = wishListsRepository.findByGoodsAndAccount(goods, detailsAccount).orElse(null);
        if (wishLists != null) {
            if (httpMethod.equals("DELETE")) {
                wishListsRepository.delete(wishLists);
            } else {
                throw new CustomException(WRONG_WISHLIST_SAVE_REQUEST);
            }
        } else {
            if (httpMethod.equals("POST")) {
                wishListsRepository.save(WishLists.builder()
                        .goods(goods)
                        .account(detailsAccount)
                        .build());
            } else {
                throw new CustomException(WRONG_WISHLIST_DELETE_REQUEST);
            }
        }
        return SUCCESS;
    }

    public int wishListCount(Long goodsId) {
        return wishListsRepository.findAllByGoodsId(goodsId).size();
    }
}
