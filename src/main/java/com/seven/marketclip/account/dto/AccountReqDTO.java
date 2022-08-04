package com.seven.marketclip.account.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class AccountReqDTO {

    @NotBlank
    @Length(min = 2, max = 11)
    @Pattern(regexp = "^[0-9가-힣a-zA-Z]{2,11}$")
    private String nickname;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Length(min = 8, max = 20)
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?])\\S*$" )
    private String password;

    @Builder
    public AccountReqDTO(String nickname, String email, String password){
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }

}
