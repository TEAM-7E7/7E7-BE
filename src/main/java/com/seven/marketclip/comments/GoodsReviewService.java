package com.seven.marketclip.comments;

import com.seven.marketclip.chat.domain.ChatRoom;
import com.seven.marketclip.chat.dto.ChatMessageReq;
import com.seven.marketclip.chat.service.ChatRoomService;
import com.seven.marketclip.comments.domain.GoodsReview;
import com.seven.marketclip.comments.dto.GoodsDealDto;
import com.seven.marketclip.comments.dto.GoodsOkDto;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.exception.ResponseCode;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.repository.GoodsRepository;
import com.seven.marketclip.security.UserDetailsImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.seven.marketclip.exception.ResponseCode.*;

@Service
public class GoodsReviewService {

    private final GoodsReviewRepository goodsReviewRepository;
    private final GoodsRepository goodsRepository;
    private final ChatRoomService chatRoomService;

    public GoodsReviewService(GoodsReviewRepository goodsReviewRepository, GoodsRepository goodsRepository, ChatRoomService chatRoomService) {
        this.goodsReviewRepository = goodsReviewRepository;
        this.goodsRepository = goodsRepository;

        this.chatRoomService = chatRoomService;
    }

    @Transactional
    @Caching(evict = {@CacheEvict(key = "'id:' + #goodsDealDto.sellerId + '__status:' + 'SOLD_OUT'", cacheNames = "myGoodsCache"),
            @CacheEvict(key = "'id:' + #goodsDealDto.sellerId + '__status:' + 'SALE'", cacheNames = "myGoodsCache")})
    public ResponseCode sendReview(UserDetailsImpl userDetails, GoodsDealDto goodsDealDto) {
        System.out.println(goodsDealDto.getGoodsId() + "//" + goodsDealDto.getBuyerId());
        //채팅방에서 판매자가 거래완료 버튼을 누름.
        //구매자가 수락 -> 할떄 남기거나 안남기거나
        Goods goods = goodsRepository.findById(goodsDealDto.getGoodsId()).orElseThrow(
                () -> new CustomException(GOODS_NOT_FOUND)
        );
        goods.getGoodsReview().reservedReview(goodsDealDto.getBuyerId());
        goods.updateStatusReserved();  //굿즈 상태 변화

        //알림 보내 벌이기!
        //상대방에게 메시지 보내기.(재호님이) -> 받은 사람이 거래 후기 남기는것,거래 상태 변경
        chatRoomService.sendToPubReview(ChatMessageReq.builder()
                .chatRoomId("TRADE")
                .goodsId(goods.getId())
                .senderId(goods.getAccount().getId())
                .partnerId(goodsDealDto.getBuyerId())
                .message("TRADE_CALL")
                .build(), goodsDealDto.getChatRoomId());
        return SUCCESS;
    }

    @Transactional
    @Caching(evict = {@CacheEvict(key = "'id:' + #goodsOkDto.sellerId + '__status:' + 'SOLD_OUT'", cacheNames = "myGoodsCache"),
            @CacheEvict(key = "'id:' + #goodsOkDto.sellerId + '__status:' + 'SALE'", cacheNames = "myGoodsCache"),
            @CacheEvict(key = "#goodsOkDto.goodsId", cacheNames = "goodsCache")})
    public ResponseCode writeReview(UserDetailsImpl userDetails, GoodsOkDto goodsOkDto) {
        GoodsReview goodsReview = goodsReviewRepository.findById(goodsOkDto.getGoodsId()).orElseThrow(
                () -> new CustomException(GOODS_REVIEW_NOT_FOUND)
        );
        String status = "none";
        if (goodsOkDto.isStatus()) {
            goodsReview.writeReview(goodsOkDto);
            goodsReview.getGoods().updateStatusSoldOut();
            status = "TRADE_SUCCESS";
        } else {
            goodsReview.cancelReview();
            goodsReview.getGoods().updateStatusSale();
            status = "TRADE_FAIL";
        }
        chatRoomService.sendToPubReview(ChatMessageReq.builder()
                .chatRoomId("TRADE")
                .goodsId(goodsReview.getGoods().getId())
                .senderId(goodsReview.getGoods().getAccount().getId())
                .partnerId(goodsOkDto.getBuyerId())
                .message(status)        //유저가 '삭제된 채팅방' 메시지를 칠 수 있기 때문에
                .build(), goodsOkDto.getChatRoomId());
        for (ChatRoom cr:goodsReview.getGoods().getChatRooms()) {
            if(!(cr.getGoods().getAccount().getId() == goodsReview.getGoods().getAccount().getId() &
                    cr.getAccount().getId() == goodsReview.getAccount().getId())){
                chatRoomService.sendToPubReview(ChatMessageReq.builder()
                        .chatRoomId("TRADE_RELOAD")
                        .partnerId(cr.getAccount().getId())
                        .message("status")        //유저가 '삭제된 채팅방' 메시지를 칠 수 있기 때문에
                        .build(), cr.getId());
            }
        }
        //알림!! (구메자가 판매자에게 후기를 남겼다는 알림)

        return SUCCESS;
    }
}























