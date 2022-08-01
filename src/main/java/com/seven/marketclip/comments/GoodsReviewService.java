package com.seven.marketclip.comments;

import com.seven.marketclip.comments.domain.GoodsReview;
import com.seven.marketclip.comments.dto.GoodsDealDto;
import com.seven.marketclip.comments.dto.GoodsOkDto;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.exception.ResponseCode;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.repository.GoodsRepository;
import com.seven.marketclip.security.UserDetailsImpl;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.seven.marketclip.exception.ResponseCode.*;

@Service
public class GoodsReviewService {

    private final GoodsReviewRepository goodsReviewRepository;
    private final GoodsRepository goodsRepository;

    public GoodsReviewService(GoodsReviewRepository goodsReviewRepository, GoodsRepository goodsRepository) {
        this.goodsReviewRepository = goodsReviewRepository;
        this.goodsRepository = goodsRepository;
    }
    @Transactional
    public ResponseCode sendReview(UserDetailsImpl userDetails, GoodsDealDto goodsReviewId) {
        System.out.println(goodsReviewId.getGoodsId() + "//"+ goodsReviewId.getBuyerId());
        //채팅방에서 판매자가 거래완료 버튼을 누름.
        //구매자가 수락 -> 할떄 남기거나 안남기거나
        Goods goods = goodsRepository.findById(goodsReviewId.getGoodsId()).orElseThrow(
                ()-> new CustomException(GOODS_NOT_FOUND)
        );
        goods.getGoodsReview().reservedReview(goodsReviewId.getBuyerId());
        goods.updateStatusReserved();  //굿즈 상태 변화

        //알림 보내 벌이기!
        //상대방에게 메시지 보내기.(재호님이) -> 받은 사람이 거래 후기 남기는것,거래 상태 변경

        return SUCCESS;
    }

    @Transactional
    public ResponseCode writeReview(UserDetailsImpl userDetails, GoodsOkDto goodsOkDto) {
        GoodsReview goodsReview = goodsReviewRepository.findById(goodsOkDto.getReviewId()).orElseThrow(
                ()-> new CustomException(GOODS_REVIEW_NOT_FOUND)
        );
        if(goodsOkDto.isStatus()){
            goodsReview.writeReview(goodsOkDto);
            goodsReview.getGoods().updateStatusSoldOut();
        }else{
            goodsReview.cancelReview();
            goodsReview.getGoods().updateStatusSale();
        }

        //알림!! (구메자가 판매자에게 후기를 남겼다는 알림)

        return SUCCESS;
    }
}























