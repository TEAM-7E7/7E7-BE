package com.seven.marketclip.files.service;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.files.domain.AccountImage;
import com.seven.marketclip.files.domain.GoodsImage;
import com.seven.marketclip.files.repository.AccountImageRepository;
import com.seven.marketclip.files.repository.GoodsImageRepository;
import com.seven.marketclip.goods.domain.Goods;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileService {
    private final GoodsImageRepository goodsImageRepository;
    private final AccountImageRepository accountImageRepository;

    public FileService(GoodsImageRepository goodsImageRepository, AccountImageRepository accountImageRepository) {
        this.goodsImageRepository = goodsImageRepository;
        this.accountImageRepository = accountImageRepository;
    }

    public void saveGoodsImageList(List<String> urlList, Goods goods, Account account) {
        for (String url : urlList) {
            GoodsImage goodsImage = GoodsImage.builder()
                    .imageUrl(url)
                    .goods(goods)
                    .account(account)
                    .build();

            goodsImageRepository.save(goodsImage);
        }
    }

    public void deleteGoodsImages(Long goodsId) {
        goodsImageRepository.deleteAllByGoodsId(goodsId);
    }

    public AccountImage findAccountImage(Long accountId) throws CustomException {
//        return accountImageRepository.findById(accountId).orElseThrow(
//                () -> new CustomException(ACCOUNT_IMAGE_NOT_FOUND)
//        );
        return accountImageRepository.findByAccountId(accountId).orElseThrow();
    }

    public void saveAccountImage(String url, Account account) {
        accountImageRepository.save(AccountImage.builder()
                .account(account)
                .imageUrl(url)
                .build());
    }

    public void deleteAccountImage(Long accountId) {
        accountImageRepository.deleteById(accountId);
    }
}
