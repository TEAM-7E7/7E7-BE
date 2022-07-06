package com.seven.marketclip.goods.controller;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.exception.HttpResponse;
import com.seven.marketclip.goods.dto.GoodsReqDTO;
import com.seven.marketclip.goods.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/goods")
public class GoodsController {
    private final GoodsService goodsService;

    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    // 게시글 전체 조회 -> 생성일자 내림차순
    @GetMapping("")
    public ResponseEntity<HttpResponse> goodsList() {
        return HttpResponse.toResponseEntity(goodsService.findGoods());
    }


    // 게시글 작성
    @PostMapping(value = "", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<HttpResponse> goodsAdd(@ModelAttribute @Validated GoodsReqDTO goodsReqDTO) {
        return HttpResponse.toResponseEntity(goodsService.addGoods(goodsReqDTO));
    }

    // todo 회원정보를 이용하는 테스트는 아직 못했음
//    @PostMapping(value = "", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//    public ResponseEntity<HttpResponse> goodsAdd(@ModelAttribute @Validated GoodsReqDTO goodsReqDTO, @AuthenticationPrincipal Account account) {
//        return HttpResponse.toResponseEntity(goodsService.addGoods(goodsReqDTO, account));
//    }



    // 상세페이지
    @GetMapping("/{goodsId}")
    public ResponseEntity<HttpResponse> goodsDetails(@PathVariable Long goodsId, @AuthenticationPrincipal Account account) {
        goodsService.plusView(goodsId);

        return HttpResponse.toResponseEntity(goodsService.findGoodsDetail(goodsId, account));
    }

    // 게시글 삭제
    @DeleteMapping("/{goodsId}")
    public ResponseEntity<HttpResponse> goodsDelete(@PathVariable Long goodsId, @AuthenticationPrincipal Account account) {

        return HttpResponse.toResponseEntity(goodsService.deleteGoods(goodsId, account));
    }

    // 게시글 수정
    @PutMapping(value = "/{goodsId}", consumes = {"multipart/form-data"})
    public ResponseEntity<HttpResponse> goodsUpdate(@PathVariable Long goodsId, @ModelAttribute @Validated GoodsReqDTO goodsReqDTO, @AuthenticationPrincipal Account account) {
        return HttpResponse.toResponseEntity(goodsService.updateGoods(goodsId, goodsReqDTO, account));
    }
}



