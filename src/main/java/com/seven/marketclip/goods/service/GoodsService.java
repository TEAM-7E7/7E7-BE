package com.seven.marketclip.goods.service;

import com.demo.exception.CustomException;
import com.demo.exception.ErrorCode;
import com.seven.marketclip.goods.repository.GoodsRepository;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.dto.GoodsForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoodsService {
    private final GoodsRepository goodsRepository;
    private final S3Uploader s3Uploader;

    //게시글 작성
    @Transactional
    public void addNewGoods(@Validated GoodsForm form, String username){
        String imgUrl = form.getFile() == null ? null: s3Uploader.uploadImage(form.getFile());
        log.error(imgUrl);
        goodsRepository.save(new Goods(form, imgUrl,username));
    }

    //게시글 수정
    @Transactional
    public void updateGoodsDetail(Long id, @Validated GoodsForm form, String username){
        Goods goods = goodsRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.GOODS_NOT_EXIST)
        );
        if (goods.getFileUrl() != null) s3Uploader.deleteImage(goods.getFileUrl());
        String updateImgUrl = form.getFile() == null? null: s3Uploader.uploadImage(form.getFile());
        goods.update(form, updateImgUrl);

        goodsRepository.save(goods);
    }

    @Transactional
    public void plusView(Long id) {
        if (!goodsRepository.existsById(id)) {
            throw new CustomException(ErrorCode.GOODS_NOT_EXIST);
        }
        goodsRepository.updateView(id);
    }
}
