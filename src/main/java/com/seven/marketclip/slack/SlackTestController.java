package com.seven.marketclip.slack;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/slack")
@RestController
public class SlackTestController {

    @GetMapping("")
    public void sendErrorLogToSlackTest() {
        log.info("this log is Info");
        log.warn("this log is Warn");
        log.error("this log is Error");
    }
}
