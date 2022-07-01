package com.seven.marketclip.goods.domain;

import com.demo.dto.GoodsForm;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.seven.marketclip.goods.dto.GoodsForm;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Validated
@NoArgsConstructor
public class Goods extends Timestamped{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Id
    private Long id;

    @Column(nullable = false)
    private String username; //user 랑 연결하면 sellerId로 변경 예정

    @Column(nullable = false, length = 25)
    @NotBlank(message = "제목을 입력하세요")
    private String title;//제목

    @Column(nullable = false)
    @NotBlank(message = "내용을 입력하세요")
    private String description;//내용

    @JsonIgnore
    @Column
    @OneToMany(mappedBy = "goods", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WishLists> wishLists = new ArrayList<>();

    @Column(nullable = false)
    String fileUrl; // 파일 따로 빼야함 ㅠㅠ

    private String category; // 카테고리 작성 예정 ㅠㅠ
    private Integer sellPrice = 0;


    private GoodsStatus status = GoodsStatus.NEW;

    private Integer viewCount = 0;//조회수
    private Integer wishCount = 0;//찜수?? 이게 맞음?

    @Builder
    public Goods(String username, String title, String description, String fileUrl, String category, Integer sellPrice){
        this.username = username;
        this.title = title;
        this.description = description;
        this.fileUrl = fileUrl;
        this.category = category;
        this.sellPrice = sellPrice;
    }

    public void update(GoodsForm form, String fileUrl){
        this.title = form.getTitle();
        this.description = form.getDescription();
        this.sellPrice = form.getSellPrice();
        this.fileUrl = fileUrl;
        this.category = form.getCategory();
    }

    @Builder
    public Goods(GoodsForm form, String fileUrl, String username){
        this.title = form.getTitle();
        this.description = form.getDescription();
        this.sellPrice = form.getSellPrice();
        this.fileUrl = fileUrl;
        this.category = form.getCategory();
        this.username = username;
    }
}
