package com.seven.marketclip.goods.service;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.goods.domain.Files;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.repository.FilesRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FileDBService {
    private final FilesRepository filesRepository;

    public FileDBService(FilesRepository filesRepository){
        this.filesRepository = filesRepository;
    }

    public void saveUrlList(List<String> urlList, Goods goods, Account account){
        for (String url : urlList) {
            Files files = Files.builder()
                    .fileUrl(url)
                    .goods(goods)
                    .account(account)
                    .build();

            filesRepository.save(files);
        }
    }

    public void deleteGoodsUrls(Long goodsId){
        filesRepository.deleteAllByGoodsId(goodsId);
    }

}
