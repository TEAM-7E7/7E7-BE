package com.seven.marketclip.email;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class EmailService {

    private final EmailRepository emailRepository;

    public EmailService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @Transactional
    public String checkEmail(EmailDTO emailDTO) {
        /**
         * 1. 이메일 객체가 없을 때 - 이메일 객체 생성
         * 2. 이메일 객체가 있고 count가 5이하 - 재시도 (count + 1 )
         * 3. 이메일 객체가 있고 count가 5이상 - 가입 실패 (24시간 뒤에 객체 삭제)
         * 4. 이메일 객체가 있고 count가 5이하이지만 생성 후 24시간이 지났을 때
         */

        if(emailRepository.findByUserEmail(emailDTO.getEmail()).isEmpty()){


        } else {
            Optional<Email> email = emailRepository.findByUserEmail(emailDTO.getEmail());
        }


//            return "이미 이메일이 발송되었습니다";


        return "";
    }

}
