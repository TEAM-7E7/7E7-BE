package com.seven.marketclip.image.service;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.image.domain.AccountImage;
import com.seven.marketclip.image.domain.GoodsImage;
import com.seven.marketclip.image.repository.AccountImageRepository;
import com.seven.marketclip.image.repository.GoodsImageRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static com.seven.marketclip.exception.ResponseCode.*;

@Service
public class ImageService {
    private final GoodsImageRepository goodsImageRepository;
    private final AccountImageRepository accountImageRepository;

    public ImageService(GoodsImageRepository goodsImageRepository, AccountImageRepository accountImageRepository) {
        this.goodsImageRepository = goodsImageRepository;
        this.accountImageRepository = accountImageRepository;
    }

    public List<Long> saveGoodsImageList(List<String> urlList, Goods goods, Account account) {
        List<Long> idList = new ArrayList<>();
        for (int i = 0; i < urlList.size(); i++) {
            GoodsImage goodsImage = GoodsImage.builder()
                    .imageUrl(urlList.get(i))
                    .goods(goods)
                    .account(account)
                    .sequence(i + 1)
                    .build();

            idList.add(goodsImageRepository.save(goodsImage).getId());
        }
        return idList;
    }

    @Transactional
    public void updateGoodsImageList(List<Long> idList, Goods goods, Account account) throws CustomException {
        for (int i = 0; i < idList.size(); i++) {
            GoodsImage goodsImage = goodsImageRepository.findById(idList.get(i)).orElseThrow(
                    () -> new CustomException(GOODS_IMAGE_NOT_FOUND)
            );
            if (goodsImage.getAccount().getId() != account.getId()) {
                throw new CustomException(INVALID_IMAGE_ACCESS);
            } else if (goodsImage.getGoods() != null) {
                throw new CustomException(DUPLICATED_IMAGE_REQ);
            }

            goodsImage.updateSequence(i + 1);
            goodsImage.updateGoods(goods);
        }
    }

    public void deleteGoodsImages(Long goodsId) {
        goodsImageRepository.deleteAllByGoodsId(goodsId);
    }

    public AccountImage findAccountImage(Long accountId) throws CustomException {
        return accountImageRepository.findById(accountId).orElseThrow(
                () -> new CustomException(ACCOUNT_IMAGE_NOT_FOUND)
        );
    }

    public void saveAccountImage(String url, Account account) {
        accountImageRepository.save(AccountImage.builder()
                .account(account)
                .imageUrl(url)
                .build());
    }

    public void deleteAccountImage(Long accountId) {
        AccountImage accountImage = findAccountImage(accountId);
        accountImage.updateUrl("default");
    }

}
