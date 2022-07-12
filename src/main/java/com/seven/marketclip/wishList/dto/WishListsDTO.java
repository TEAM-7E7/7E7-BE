package com.seven.marketclip.wishList.dto;

import com.seven.marketclip.account.Account;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class WishListsDTO {
    Account account;
    Long goodsId;

    @Builder
    public WishListsDTO(Account account, Long goodsId) {
        this.account = account;
        this.goodsId = goodsId;
    }
}
