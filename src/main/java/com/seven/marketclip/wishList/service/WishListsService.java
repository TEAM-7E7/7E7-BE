package com.seven.marketclip.wishList.service;


import com.seven.marketclip.account.Account;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.exception.ResponseCode;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.repository.GoodsRepository;
import com.seven.marketclip.wishList.domain.WishLists;
import com.seven.marketclip.wishList.repository.WishListsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.seven.marketclip.exception.ResponseCode.GOODS_NOT_FOUND;
import static com.seven.marketclip.exception.ResponseCode.SUCCESS;

@Service
@RequiredArgsConstructor
public class WishListsService {
    private final GoodsRepository goodsRepository;
    private final WishListsRepository wishListsRepository;

    @Transactional
    public ResponseCode doWishList(Long goodsId, Account account) throws CustomException {
        Goods goods = goodsRepository.findById(goodsId).orElseThrow(
                () -> new CustomException(GOODS_NOT_FOUND)
        );
        WishLists wishLists = wishListsRepository.findByGoodsAndAccount(goods, account).orElse(null);
        if(wishLists != null){
            wishListsRepository.delete(wishLists);
        }else {
            wishListsRepository.save(WishLists.builder()
                    .goods(goods)
                    .account(account)
                    .build());
        }
        return SUCCESS;
    }
}
