package com.seven.marketclip.goods.service;

import com.seven.marketclip.goods.exception.CustomException;
import com.seven.marketclip.goods.exception.ErrorCode;
import com.seven.marketclip.goods.repository.GoodsRepository;
import com.seven.marketclip.goods.repository.WishListsRepository;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.domain.WishLists;
import com.seven.marketclip.goods.dto.WishListsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class WishListsService {
    private final GoodsRepository goodsRepository;
    private final WishListsRepository wishListsRepository;

    @Transactional
    public void doWishList(WishListsDto wishListsDto){
        Goods goods = goodsRepository.findById(wishListsDto.getGoodsId()).orElseThrow(
                () -> new CustomException(ErrorCode.GOODS_NOT_EXIST)
        );
        WishLists wishLists = wishListsRepository.findByGoodsAndAccount(goods, wishListsDto.getAccount()).orElse(null);
        if(wishLists != null){
            wishListsRepository.delete(wishLists);
            goodsRepository.updateWishCount(goods.getId(), -1);
        }else {
            wishListsRepository.save(WishLists.builder()
                    .goods(goods)
                    .account(wishLists.getAccount())
                    .build());
            goodsRepository.updateWishCount(goods.getId(),1);
        }
    }
}
