package com.seven.marketclip.wish.controller;

import com.seven.marketclip.exception.HttpResponse;
import com.seven.marketclip.security.UserDetailsImpl;
import com.seven.marketclip.wish.service.WishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@Api(tags = "즐겨찾기 컨트롤러")
@Slf4j
@RequestMapping("/api/wish-list")
public class WishController {
    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @ApiOperation(value = "게시글 찜하기 (토글)", notes = "게시글 찜하기 / 취소를 수행하는 토글 api")
    @RequestMapping(value = "/{goodsId}", method = {RequestMethod.POST, RequestMethod.DELETE})
    public ResponseEntity<HttpResponse> wishToggle(@PathVariable Long goodsId, @AuthenticationPrincipal UserDetailsImpl account, HttpServletRequest httpServletRequest) {
        return HttpResponse.toResponseEntity(wishService.doWishList(goodsId, account, httpServletRequest.getMethod()));
    }

}
