package com.seven.marketclip.goods.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seven.marketclip.exception.CustomException;
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

import static com.seven.marketclip.exception.ResponseCode.CONVERT_STRING_TO_MAP_FAIL;

@Validated
@Getter
@Setter
@NoArgsConstructor
public class GoodsReqDTO {
    @NotBlank(message = "제목에 값을 입력하세요")
    private String title;
    @NotBlank(message = "내용에 값을 입력하세요")
    private String description;
    private List<Map<String, Object>> files = new ArrayList<>();
    private GoodsCategory category;
    private Integer sellPrice;
//    private List<Map<String, Object>> filesMap;

    @Builder
    public GoodsReqDTO(String title, String description, List<Map<String, Object>> files, GoodsCategory category, Integer sellPrice) {
        this.title = title;
        this.description = description;
        this.files = files;
        this.category = category;
        this.sellPrice = sellPrice;
    }

//    public void stringToMap() throws CustomException {
//        ObjectMapper mapper = new ObjectMapper();
//        for (String string : this.files) {
//            try {
//                Map<String, Object> map = mapper.readValue(string, Map.class);
//                this.filesMap.add(map);
//            } catch (JsonProcessingException e) {
//                throw new CustomException(CONVERT_STRING_TO_MAP_FAIL);
//            }
//        }
//    }

}
