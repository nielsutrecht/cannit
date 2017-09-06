package com.nibado.project.redditcan.service;

import com.nibado.project.redditcan.auth.Authenticator;
import com.nibado.project.redditcan.reddit.Reddit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ReplyService {
    public final Authenticator authenticator;

    public ReplyService(final Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    @Scheduled(cron = "*/10 * * * * *")
    public void run() {
        Optional<Reddit> reddit = authenticator.reddit();

        if (!reddit.isPresent()) {
            log.info("No reddit credentials configured");
            return;
        }

        reddit.get().comments().forEach(System.out::println);
    }
}
