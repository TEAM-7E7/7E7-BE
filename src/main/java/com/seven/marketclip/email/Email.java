package com.seven.marketclip.email;

import com.seven.marketclip.Timestamped;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity (name = "email_validation")
@Getter
public class Email extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String token;

    @ColumnDefault("1")
    private Integer count;

}
