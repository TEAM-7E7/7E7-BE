package com.seven.marketclip.goods.controller;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.exception.HttpResponse;
import com.seven.marketclip.goods.dto.GoodsReqDTO;
import com.seven.marketclip.goods.service.GoodsService;
import com.seven.marketclip.security.UserDetailsImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "물품 게시판 컨트롤러")
@Slf4j
@RequestMapping("/api/goods")
public class GoodsController {
    private final GoodsService goodsService;

    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    // 게시글 전체 조회 -> 생성일자 내림차순
    @ApiOperation(value = "게시글 전체 조회", notes = "상세설명을 제외하고, 첫 번째 사진(대문 사진)만을 포함한 물품 데이터 / 페이징")
    @GetMapping("")
    public ResponseEntity<HttpResponse> goodsList(@PageableDefault final Pageable pageable) {
        return HttpResponse.toResponseEntity(goodsService.findGoods(pageable));
    }

    // 게시글 작성
    @ApiOperation(value = "게시글 작성", notes = "게시글 작성 api")
    @PostMapping(value = "", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<HttpResponse> goodsAdd(@ModelAttribute @Validated GoodsReqDTO goodsReqDTO) {
//    public ResponseEntity<HttpResponse> goodsAdd(@ModelAttribute @Validated GoodsReqDTO goodsReqDTO, @AuthenticationPrincipal UserDetailsImpl account) {
//        return HttpResponse.toResponseEntity(goodsService.addGoods(goodsReqDTO, account));
        return HttpResponse.toResponseEntity(goodsService.addGoods(goodsReqDTO));
    }

    // 상세페이지
    @ApiOperation(value = "게시글 상세페이지", notes = "게시글 상세페이지 api")
    @GetMapping("/{goodsId}")
    public ResponseEntity<HttpResponse> goodsDetails(@PathVariable Long goodsId, @AuthenticationPrincipal UserDetailsImpl account) {
        goodsService.plusView(goodsId);

        return HttpResponse.toResponseEntity(goodsService.findGoodsDetail(goodsId, account));
    }

    // 게시글 삭제
    @ApiOperation(value = "게시글 삭제", notes = "게시글 삭제 api")
    @DeleteMapping("/{goodsId}")
    public ResponseEntity<HttpResponse> goodsDelete(@PathVariable Long goodsId, @AuthenticationPrincipal UserDetailsImpl account) {
        return HttpResponse.toResponseEntity(goodsService.deleteGoods(goodsId, account));
    }

    // 게시글 수정
    @ApiOperation(value = "게시글 수정", notes = "게시글 수정 api")
    @PutMapping(value = "/{goodsId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<HttpResponse> goodsUpdate(@PathVariable Long goodsId, @ModelAttribute @Validated GoodsReqDTO goodsReqDTO, @AuthenticationPrincipal UserDetailsImpl account) {
        return HttpResponse.toResponseEntity(goodsService.updateGoods(goodsId, goodsReqDTO, account));
    }
}
