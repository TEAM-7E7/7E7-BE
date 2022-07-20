package com.seven.marketclip.wish_list.service;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.exception.ResponseCode;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.service.GoodsService;
import com.seven.marketclip.security.UserDetailsImpl;
import com.seven.marketclip.wish_list.domain.WishLists;
import com.seven.marketclip.wish_list.repository.WishListsRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.seven.marketclip.exception.ResponseCode.*;

@Service
public class WishListsService {
    private final GoodsService goodsService;
    private final WishListsRepository wishListsRepository;

    public WishListsService(@Lazy GoodsService goodsService, WishListsRepository wishListsRepository) {
        this.goodsService = goodsService;
        this.wishListsRepository = wishListsRepository;
    }

    @Transactional
    public ResponseCode doWishList(Long goodsId, UserDetailsImpl account, String httpMethod) throws CustomException {
        Account detailsAccount = new Account(account);
        Goods goods = goodsService.findGoodsById(goodsId);
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

    // 내가 즐겨찾기 한 게시글 보기 - GoodsService 에서 호출
    public Page<WishLists> findMyWish(Long accountId, Pageable pageable){
        return wishListsRepository.findAllByAccountIdOrderByCreatedAtDesc(accountId, pageable);
    }

    public int wishListCount(Long goodsId) {
        return wishListsRepository.findAllByGoodsId(goodsId).size();
    }
}
