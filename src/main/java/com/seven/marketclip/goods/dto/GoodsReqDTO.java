package com.seven.marketclip.goods.dto;

import com.seven.marketclip.goods.domain.GoodsCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class GoodsReqDTO {
    private String title;
    private String description;
    private Map<Integer,MultipartFile> files = new HashMap<>();
    private Map<Integer,String> urls = new HashMap<>();

    private GoodsCategory category;
    private Integer sellPrice;

    @Builder
    public GoodsReqDTO(String title, String description, Map<Integer,MultipartFile> files, Map<Integer,String> urls, GoodsCategory category, Integer sellPrice) {
        this.title = title;
        this.description = description;
        this.files = files;
        this.urls = urls;
        this.category = category;
        this.sellPrice = sellPrice;
    }

}
