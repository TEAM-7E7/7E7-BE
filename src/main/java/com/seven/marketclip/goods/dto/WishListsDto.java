package com.seven.marketclip.goods.dto;

import com.seven.marketclip.account.Account;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class WishListsDto {
    Account account;
    Long goodsId;

    @Builder
    public WishListsDto(Account account, Long goodsId) {
        this.account = account;
        this.goodsId = goodsId;
    }
}
