package com.seven.marketclip.goods.controller;

import com.demo.dto.GoodsForm;
import com.demo.dto.GoodsResponse;
import com.demo.exception.CustomException;
import com.demo.exception.ErrorCode;
import com.demo.repository.GoodsRepository;
import com.demo.service.GoodsService;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.dto.GoodsForm;
import com.seven.marketclip.goods.dto.GoodsResponse;
import com.seven.marketclip.goods.repository.GoodsRepository;
import com.seven.marketclip.goods.service.GoodsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/goods")
public class GoodsController {
    private final GoodsService goodsService;
    private final GoodsRepository goodsRepository;


    //메인페이지 조회 -> 생성일자 내림차순
    @GetMapping("")
    public List<Goods> getGoodsList(){
        return goodsRepository.findAllByOrderByCreatedAtDesc();
    }


    //상품 게시글 작성
    @PostMapping(value = "", consumes = {"multipart/form-data"})
    public ResponseEntity<String> addGoods(@Valid @ModelAttribute GoodsForm form, String username){
        goodsService.addNewGoods(form, username);
        return ResponseEntity.ok().body("게시글 작성 완료");
    }

    // 상품 상세페이지
    @GetMapping("/{goodsId}")
    public GoodsResponse getGoods(@PathVariable Long goodsId){
        goodsService.plusView(goodsId);
        Goods goods = goodsRepository.findById(goodsId).orElseThrow(
                () -> new CustomException(ErrorCode.GOODS_NOT_EXIST)
        );
        return new GoodsResponse(goods);
    }

    // 상품 게시글 삭제
    @DeleteMapping("/{goodsId}")
    public ResponseEntity<String> deleteGoods(@PathVariable Long goodsId){
        goodsRepository.findById(goodsId).orElseThrow(
                ()-> new CustomException(ErrorCode.GOODS_NOT_EXIST)
        );
        goodsRepository.deleteById(goodsId);
        return ResponseEntity.ok().body("삭제완료");
    }

    // 상품 게시글 수정
    @PutMapping(value = "/{goodsId}", consumes = {"multipart/form-data"})
    public ResponseEntity<String> editGoods(@PathVariable Long goodsId, @Valid @ModelAttribute GoodsForm form,String username){
        goodsService.updateGoodsDetail(goodsId, form, username);
        return ResponseEntity.ok().body("수정완료");
    }
}



