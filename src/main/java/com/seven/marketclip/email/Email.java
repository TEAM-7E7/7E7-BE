package com.seven.marketclip.email;

import com.seven.marketclip.Timestamped;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity (name = "email_validation")
@Getter
@NoArgsConstructor
public class Email extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String userEmail;

    private String token;

    @ColumnDefault("0")
    private Integer count;

    public Email(EmailDTO emailDTO){
        this.userEmail = emailDTO.getEmail();
        this.token = emailDTO.getToken();
    }

    public void updateCount(){
        count += 1;
    }

}
