package com.seven.marketclip.comments.domain;

import com.seven.marketclip.Timestamped;
import com.seven.marketclip.account.domain.Account;
import com.seven.marketclip.goods.domain.Goods;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class GoodsReview extends Timestamped {

    @Id
    private Long id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "goods_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Goods goods;

    //상대 회원 아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Account account;

    private String message; //후기 메시지

    private Double kindness; //친절

    private Double responseSpeed; //응답속도

    private Double quality; //풂질

    private Double appointment; //시간약속

}
