package com.seven.marketclip.goods.service;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.dto.GoodsForm;
import com.seven.marketclip.goods.repository.GoodsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;

import static com.seven.marketclip.exception.ResponseCode.GOODS_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoodsService {
    private final GoodsRepository goodsRepository;
    private final S3Uploader s3Uploader;

    //게시글 작성
    @Transactional
    public void addNewGoods(@Validated GoodsForm form, Account account){
        String fileUrl = form.getFile() == null ? null: s3Uploader.uploadFile(form.getFile());
        log.error(fileUrl);
        goodsRepository.save(new Goods(form, fileUrl ,account));
    }

    //게시글 수정
    @Transactional
    public void updateGoodsDetail(Long id, @Validated GoodsForm form, Account account){
        Goods goods = goodsRepository.findById(id).orElseThrow(
                () -> new CustomException(GOODS_NOT_FOUND)
        );
        if (goods.getFileUrl() != null) s3Uploader.deleteFile(goods.getFileUrl());
        String updateFileUrl = form.getFile() == null? null: s3Uploader.uploadFile(form.getFile());
        goods.update(form, updateFileUrl);

        goodsRepository.save(goods);
    }

    @Transactional
    public void plusView(Long id) {
        if (!goodsRepository.existsById(id)) {
            throw new CustomException(GOODS_NOT_FOUND);
        }
        goodsRepository.updateView(id);
    }
}
