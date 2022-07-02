package com.seven.marketclip.email;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class EmailDTO {
    private String email;
    private String emailToken;
//    private Integer count;
//    private LocalDateTime createAt;
//    private LocalDateTime modifiedAt;

    @Builder
    public EmailDTO(String email, String emailToken) {
        this.email = email;
        this.emailToken = emailToken;
    }

}
