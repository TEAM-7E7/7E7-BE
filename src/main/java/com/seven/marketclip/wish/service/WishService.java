package com.seven.marketclip.wish.service;

import com.seven.marketclip.account.domain.Account;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.exception.ResponseCode;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.repository.GoodsRepository;
import com.seven.marketclip.security.UserDetailsImpl;
import com.seven.marketclip.wish.domain.Wish;
import com.seven.marketclip.wish.repository.WishRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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
//    @Caching(evict = { @CacheEvict(key = "#userDetails.id", cacheNames = "myWishCache"), @CacheEvict(key = "#goodsId", cacheNames = "goodsCache")})
    @CacheEvict(key = "#goodsId", cacheNames = "goodsCache")
    public ResponseCode doWishList(Long goodsId, UserDetailsImpl userDetails, String httpMethod) throws CustomException {
        Account account = new Account(userDetails);
        Goods goods = goodsRepository.findById(goodsId).orElseThrow(
                () -> new CustomException(GOODS_NOT_FOUND)
        );
        Wish wish = wishRepository.findByGoodsAndAccount(goods, account).orElse(null);
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
                        .account(account)
                        .build());
            } else {
                throw new CustomException(WRONG_WISHLIST_DELETE_REQUEST);
            }
        }
        return SUCCESS;
    }
}
