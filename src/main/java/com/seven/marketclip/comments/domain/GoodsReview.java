package com.seven.marketclip.comments.domain;

import com.seven.marketclip.Timestamped;
import com.seven.marketclip.account.domain.Account;
import com.seven.marketclip.comments.dto.GoodsOkDto;
import com.seven.marketclip.goods.domain.Goods;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class GoodsReview extends Timestamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Builder
    public GoodsReview(Long id, Goods goods, Account account, String message, Double kindness, Double responseSpeed, Double quality, Double appointment) {
        this.id = id;
        this.goods = goods;
        this.account = account;
        this.message = message;
        this.kindness = kindness;
        this.responseSpeed = responseSpeed;
        this.quality = quality;
        this.appointment = appointment;
    }

    public void writeReview(GoodsOkDto goodsOkDto){
        this.message = goodsOkDto.getMessage();
        this.kindness = goodsOkDto.getKindness();
        this.responseSpeed = goodsOkDto.getResponseSpeed();
        this.quality = goodsOkDto.getQuality();
        this.appointment = goodsOkDto.getAppointment();
    }
    public void cancelReview(){
        this.account = null;
    }
    public void reservedReview(Long id){
        this.account = Account.builder()
                .id(id)
                .build();
    }
    public boolean isEmptyAccount(){
        if(account == null){
            return true;
        }
        return false;
    }
}
