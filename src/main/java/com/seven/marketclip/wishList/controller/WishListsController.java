package com.seven.marketclip.wishList.controller;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.exception.HttpResponse;
import com.seven.marketclip.security.UserDetailsImpl;
import com.seven.marketclip.wishList.service.WishListsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "즐겨찾기 컨트롤러")
@Slf4j
@RequestMapping("/api/wish-list")
public class WishListsController {
    private final WishListsService wishListsService;

    public WishListsController(WishListsService wishListsService){
        this.wishListsService = wishListsService;
    }

    @ApiOperation(value = "게시글 찜하기 (토글)", notes = "게시글 찜하기 / 취소를 수행하는 토글 api")
    @RequestMapping(value = "/{goodsId}/wish_lists", method = {RequestMethod.POST, RequestMethod.DELETE})
    public ResponseEntity<HttpResponse> wishListToggle(@PathVariable Long goodsId, @AuthenticationPrincipal UserDetailsImpl account){
        Account detailsAccount = new Account(account);
        return HttpResponse.toResponseEntity(wishListsService.doWishList(goodsId, detailsAccount));
    }
}
