package com.seven.marketclip.goods.dto;

import com.seven.marketclip.goods.domain.GoodsCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@Getter
@Setter
@NoArgsConstructor
public class GoodsReqDTO {
    @NotBlank(message = "제목에 값을 입력하세요")
    private String title;
    @NotBlank(message = "내용에 값을 입력하세요")
    private String description;
    private List<Map<String,Object>> files = new ArrayList<>();
    private GoodsCategory category;
    private Integer sellPrice;

    @Builder
    public GoodsReqDTO(String title, String description, List<Map<String,Object>> files, GoodsCategory category, Integer sellPrice) {
        this.title = title;
        this.description = description;
        this.files = files;
        this.category = category;
        this.sellPrice = sellPrice;
    }

}
