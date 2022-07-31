package com.seven.marketclip.comments.entity;

import com.seven.marketclip.Timestamped;
import com.seven.marketclip.account.domain.Account;
import com.seven.marketclip.goods.domain.Goods;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class TradeReview extends Timestamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    //상대 회원 아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Account account;

    //자기 자신
    @MapsId
    @OneToOne
    @JoinColumn(name = "goods_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Goods goods;

    private String message; //후기 메시지

    private Double kindess; //친절

    private Double responseSpeed; //응답속도

    private Double quality; //풂질

    private Double appointment; //시간약속

}
