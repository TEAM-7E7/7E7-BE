package com.seven.marketclip.goods.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

@Validated
@Getter
@Setter
@AllArgsConstructor
@Builder
public class GoodsForm {
    @NotBlank(message = "제목에 값을 입력하세요")
    private String title;
    @NotBlank(message = "내용에 값을 입력하세요")
    private String description;
    private MultipartFile file;
    private String category;
    private Integer sellPrice;
}
