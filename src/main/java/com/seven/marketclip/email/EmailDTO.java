package com.seven.marketclip.email;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;


@Getter
@NoArgsConstructor
public class EmailDTO {

    @Email
    private String email;
    private String emailToken;

    @Builder
    public EmailDTO(String email, String emailToken) {
        this.email = email;
        this.emailToken = emailToken;
    }

}
