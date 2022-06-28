package com.seven.marketclip.email;


import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class EmailDTO {
    private String email;

    private String token;

    private Integer count;

    private LocalDateTime createAt;

    private LocalDateTime modifiedAt;

}
