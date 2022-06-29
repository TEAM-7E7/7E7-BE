package com.seven.marketclip.email;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class EmailDTO {
    private String email;
    private String token;
//    private Integer count;
//    private LocalDateTime createAt;
//    private LocalDateTime modifiedAt;

    @Builder
    public EmailDTO(String email, String token){
        this.email = email;
        this.token = token;
    }

}
