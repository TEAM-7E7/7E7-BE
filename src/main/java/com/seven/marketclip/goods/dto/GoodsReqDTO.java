package com.seven.marketclip.goods.dto;

import com.seven.marketclip.goods.domain.GoodsCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GoodsReqDTO {
    private String title;
    private String description;
    private ArrayList<StringMultipart> files = new ArrayList<>();
    private GoodsCategory category;
    private Integer sellPrice;

    @Builder
    public GoodsReqDTO(String title, String description, ArrayList<StringMultipart> files, GoodsCategory category, Integer sellPrice) {
        this.title = title;
        this.description = description;
        this.files = files;
        this.category = category;
        this.sellPrice = sellPrice;
    }

}
