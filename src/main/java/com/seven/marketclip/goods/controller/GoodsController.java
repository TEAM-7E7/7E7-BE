package com.seven.marketclip.goods.controller;

import com.seven.marketclip.exception.HttpResponse;
import com.seven.marketclip.goods.dto.GoodsReqDTO;
import com.seven.marketclip.goods.enums.GoodsCategory;
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

    // 게시글 전체 조회 -> 정렬 => 생성일자 내림차순
    @ApiOperation(value = "게시글 전체 조회", notes = "상세설명을 제외하고, 첫 번째 사진(대문 사진)만을 포함한 물품 데이터 / 페이징")
    @GetMapping("")
    public ResponseEntity<HttpResponse> goodsList(@PageableDefault final Pageable pageable) {
        return HttpResponse.toResponseEntity(goodsService.findGoods(pageable));
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
    @GetMapping("/{goodsId}")
    public ResponseEntity<HttpResponse> goodsDetails(@PathVariable Long goodsId) {
        return HttpResponse.toResponseEntity(goodsService.findGoodsDetail(goodsId));
    }

    @ApiOperation(value = "게시글 삭제", notes = "게시글을 삭제하는 api")
    @DeleteMapping("/{goodsId}")
    public ResponseEntity<HttpResponse> goodsDelete(@PathVariable Long goodsId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return HttpResponse.toResponseEntity(goodsService.deleteGoods(goodsId, userDetails));
    }

    @ApiOperation(value = "게시글 수정", notes = "게시글을 수정하는 api")
    @PutMapping(value = "/{goodsId}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<HttpResponse> goodsUpdate(@PathVariable Long goodsId, @RequestBody GoodsReqDTO goodsReqDTO, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return HttpResponse.toResponseEntity(goodsService.updateGoods(goodsId, goodsReqDTO, userDetails));
    }

    @ApiOperation(value = "내가 쓴 게시글 조회", notes = "내가 쓴 게시글을 조회하는 api / 페이징")
    @PostMapping("/my-page")
    public ResponseEntity<HttpResponse> myGoodsList(@AuthenticationPrincipal UserDetailsImpl userDetails, @PageableDefault final Pageable pageable) {
        return HttpResponse.toResponseEntity(goodsService.findMyGoods(userDetails, pageable));
    }

    @ApiOperation(value = "카테고리별 조회", notes = "카테고리별 조회 api / 페이징")
    @GetMapping("/category/{category}")
    public ResponseEntity<HttpResponse> categoryGoodsList(@PathVariable GoodsCategory category, @PageableDefault final Pageable pageable) {
        return HttpResponse.toResponseEntity(goodsService.findGoodsCategory(category, pageable));
    }

    @ApiOperation(value = "즐겨찾기순 정렬 조회", notes = "카테고리별 조회 api / 페이징")
    @GetMapping("/favorite")
    public ResponseEntity<HttpResponse> favoriteGoodsList(@PageableDefault final Pageable pageable) {
        return HttpResponse.toResponseEntity(goodsService.goodsListFavorite(pageable));
    }

    @ApiOperation(value = "조회순 정렬 조회", notes = "조회순 정렬 api / 페이징")
    @GetMapping("/view")
    public ResponseEntity<HttpResponse> viewGoodsList(@PageableDefault final Pageable pageable) {
        return HttpResponse.toResponseEntity(goodsService.goodsListView(pageable));
    }

}
