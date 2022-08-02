package com.seven.marketclip.comments;

import com.seven.marketclip.comments.dto.GoodsDealDto;
import com.seven.marketclip.comments.dto.GoodsOkDto;
import com.seven.marketclip.exception.HttpResponse;
import com.seven.marketclip.security.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review")
public class GoodsReviewController {

    private final GoodsReviewService goodsReviewService;

    public GoodsReviewController(GoodsReviewService goodsReviewService) {
        this.goodsReviewService = goodsReviewService;
    }

    //거래완료신청(판매자 -> 구매자)
    @PostMapping("/deal")
    public ResponseEntity<HttpResponse> sendReview(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody GoodsDealDto goodsReviewId) {
        return HttpResponse.toResponseEntity(goodsReviewService.sendReview(userDetails, goodsReviewId));
    }

    //후기 남기기(구매자 -> 판매자)
    @PutMapping("/ok")
    public ResponseEntity<HttpResponse> writeReview(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody GoodsOkDto goodsOkDto) {
        return HttpResponse.toResponseEntity(goodsReviewService.writeReview(userDetails, goodsOkDto));
    }

}
