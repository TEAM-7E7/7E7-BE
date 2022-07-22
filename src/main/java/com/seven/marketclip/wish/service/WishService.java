package com.seven.marketclip.wish.service;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.exception.ResponseCode;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.repository.GoodsRepository;
import com.seven.marketclip.security.UserDetailsImpl;
import com.seven.marketclip.wish.domain.Wish;
import com.seven.marketclip.wish.repository.WishRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.List;

import static com.seven.marketclip.exception.ResponseCode.*;

@Service
public class WishService {
    private final GoodsRepository goodsRepository;
    private final WishRepository wishRepository;

    public WishService(GoodsRepository goodsRepository, WishRepository wishRepository) {
        this.goodsRepository = goodsRepository;
        this.wishRepository = wishRepository;
    }

    @Transactional
    public ResponseCode doWishList(Long goodsId, UserDetailsImpl account, String httpMethod) throws CustomException {
        Account detailsAccount = new Account(account);
        Goods goods = goodsRepository.findById(goodsId).orElseThrow(
                () -> new CustomException(GOODS_NOT_FOUND)
        );
        Wish wish = wishRepository.findByGoodsAndAccount(goods, detailsAccount).orElse(null);
        if (wish != null) {
            if (httpMethod.equals("DELETE")) {
                wishRepository.delete(wish);
            } else {
                throw new CustomException(WRONG_WISHLIST_SAVE_REQUEST);
            }
        } else {
            if (httpMethod.equals("POST")) {
                wishRepository.save(Wish.builder()
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
    public Page<Wish> findMyWish(Long accountId, Pageable pageable){
        return wishRepository.findAllByAccountIdOrderByCreatedAtDesc(accountId, pageable);
    }

//    public List<Wish> goodsWishLists(Long goodsId) {
//        return wishRepository.findAllByGoodsId(goodsId);
//    }

    public List<Long> goodsWishLists(Long goodsId) {
        return wishRepository.findAllAccountIdByGoodsId(goodsId);
    }
}
