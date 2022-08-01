package com.seven.marketclip.goods.controller;

import com.seven.marketclip.exception.HttpResponse;
import com.seven.marketclip.goods.dto.GoodsReqDTO;
import com.seven.marketclip.goods.dto.OrderByDTO;
import com.seven.marketclip.goods.enums.GoodsStatus;
import com.seven.marketclip.goods.service.GoodsService;
import com.seven.marketclip.security.UserDetailsImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Api(tags = "물품 게시판 컨트롤러")
@Slf4j
@RequestMapping("/api/goods")
public class GoodsController {
    private final GoodsService goodsService;

    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    // 게시글 전체 조회 -> 동적 쿼리
    @ApiOperation(value = "게시글 전체 조회", notes = "상세설명을 제외하고, 첫 번째 사진(대문 사진)만을 포함한 물품 데이터 / 페이징")
    @PostMapping("/dynamic-paging")
//    @Cacheable(key = "#orderByDTO.goodsCategoryList+orderByDTO.goodsOrderBy+pageable", cacheNames = "goodsCache")
    public ResponseEntity<HttpResponse> goodsPaging(@RequestBody OrderByDTO orderByDTO, @PageableDefault final Pageable pageable) {
        return HttpResponse.toResponseEntity(goodsService.pagingGoods(orderByDTO, pageable));
    }

    @ApiOperation(value = "게시글 이미지 파일 저장", notes = "게시글 이미지 파일 저장 api")
    @PostMapping(value = "/image-upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<HttpResponse> s3Add(@RequestParam("goodsImage") List<MultipartFile> multipartFileList, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return HttpResponse.toResponseEntity(goodsService.addS3(multipartFileList, userDetails.getId()));
    }

    @ApiOperation(value = "게시글 작성", notes = "게시글을 작성하는 api")
    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<HttpResponse> goodsAdd(@RequestBody GoodsReqDTO goodsReqDTO, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return HttpResponse.toResponseEntity(goodsService.addGoods(goodsReqDTO, userDetails));
    }

    @ApiOperation(value = "게시글 상세페이지", notes = "게시글 상세페이지 api")
    @GetMapping("/details/{goodsId}")
//    @Cacheable(key = "#goodsId", cacheNames = "goodsCache")
    public ResponseEntity<HttpResponse> goodsDetails(@PathVariable Long goodsId) {
        return HttpResponse.toResponseEntity(goodsService.findGoodsDetail(goodsId));
    }

    @ApiOperation(value = "게시글 삭제", notes = "게시글을 삭제하는 api")
    @DeleteMapping("/{goodsId}")
//    @CacheEvict(key = "#goodsId", cacheNames = "goodsCache")
    public ResponseEntity<HttpResponse> goodsDelete(@PathVariable Long goodsId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return HttpResponse.toResponseEntity(goodsService.deleteGoods(goodsId, userDetails));
    }

    @ApiOperation(value = "게시글 수정", notes = "게시글을 수정하는 api")
    @PutMapping(value = "/{goodsId}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<HttpResponse> goodsUpdate(@PathVariable Long goodsId, @RequestBody GoodsReqDTO goodsReqDTO, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return HttpResponse.toResponseEntity(goodsService.updateGoods(goodsId, goodsReqDTO, userDetails));
    }

    @ApiOperation(value = "내가 쓴 게시글 조회", notes = "내가 쓴 게시글을 조회하는 api / 페이징")
    @GetMapping("/my-page")
//    @Cacheable(key = "#userDetails.id", cacheNames = "myGoodsCache")
    public ResponseEntity<HttpResponse> myGoodsList(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam("goodsStatus") GoodsStatus goodsStatus, @PageableDefault final Pageable pageable) {
        return HttpResponse.toResponseEntity(goodsService.findMyGoods(userDetails, goodsStatus, pageable));
    }

    @ApiOperation(value = "내가 즐겨찾기 한 게시글 보기", notes = "내가 즐겨찾기 한 게시글 보기 api / 페이징")
    @GetMapping("/my-wish")
//    @Cacheable(key = "#userDetails.id", cacheNames = "myGoodsCache")
    public ResponseEntity<HttpResponse> myWishFind(@AuthenticationPrincipal UserDetailsImpl userDetails, @PageableDefault final Pageable pageable) {
        return HttpResponse.toResponseEntity(goodsService.findMyWish(userDetails, pageable));
    }

}
