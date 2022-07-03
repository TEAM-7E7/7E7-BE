package com.seven.marketclip.goods.controller;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.goods.dto.WishListsDto;
import com.seven.marketclip.goods.service.WishListsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/goods")
public class WishListsController {
    private final WishListsService wishListsService;

    // 좋아요 실행 & 취소 묶어 벌임~
    @RequestMapping(value = "/{goodsId}/wish_lists", method = {RequestMethod.POST, RequestMethod.DELETE})
    public ResponseEntity<String> doWishList(@PathVariable Long goodsId, @AuthenticationPrincipal Account account){
        WishListsDto wishListsDto = WishListsDto.builder()
                .goodsId(goodsId)
                .account(account)
                .build();
        wishListsService.doWishList(wishListsDto);
        return ResponseEntity.ok().body("찜!");
    }
}
