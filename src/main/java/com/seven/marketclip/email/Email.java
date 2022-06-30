package com.seven.marketclip.email;

import com.seven.marketclip.Timestamped;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "email_validation")
@Getter
@NoArgsConstructor
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String userEmail;

    private String authToken;

//    @ColumnDefault("0")
//    private Integer count;

    private LocalDateTime expireDate;

    @Builder
    public Email(String userEmail, String authToken) {
        this.userEmail = userEmail;
        this.authToken = authToken;
        this.expireDate = LocalDateTime.now().plusMinutes(10);
    }

    public Email(EmailDTO emailDTO) {
        this.userEmail = emailDTO.getEmail();
        this.authToken = emailDTO.getAuthToken();
    }

//    public void updateCount(){
//        count += 1;
//    }

}
