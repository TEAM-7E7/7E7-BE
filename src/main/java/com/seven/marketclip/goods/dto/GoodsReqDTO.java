package com.seven.marketclip.goods.dto;

import com.seven.marketclip.goods.enums.GoodsCategory;
import com.seven.marketclip.goods.enums.GoodsStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GoodsReqDTO {
    private String title;

    @NotBlank
    @Length(min = 10, max = 200)
    private String description;
    private List<Long> fileIdList = new ArrayList<>();
    private GoodsCategory category;
    private GoodsStatus status;
    private int sellPrice;

    @Builder
    public GoodsReqDTO(String title, String description, List<Long> fileIdList, GoodsCategory category, GoodsStatus status, int sellPrice) {
        this.title = title;
        this.description = description;
        this.fileIdList = fileIdList;
        this.category = category;
        this.status = status;
        this.sellPrice = sellPrice;
    }

}
