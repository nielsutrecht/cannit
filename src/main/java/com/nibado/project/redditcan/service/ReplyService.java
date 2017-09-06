package com.nibado.project.redditcan.service;

import com.nibado.project.redditcan.reddit.Reddit;
import com.nibado.project.redditcan.service.domain.FailEvent;
import com.nibado.project.redditcan.service.domain.SuccessEvent;
import lombok.extern.slf4j.Slf4j;
import net.dean.jraw.models.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReplyService {
    private final Reddit reddit;
    private final ResponseService responseService;
    private final LogService logService;

    @Autowired
    public ReplyService(final Reddit reddit, final ResponseService responseService, final LogService logService) {
        this.reddit = reddit;
        this.responseService = responseService;
        this.logService = logService;
    }

    @Scheduled(cron = "*/10 * * * * *")
    public void run() {
        List<Comment> canned = reddit
                .comments()
                .stream()
                .filter(c -> c.getBody().trim().startsWith("/can"))
                .collect(Collectors.toList());

        log.info("{} canned replies: {}", canned.size(), canned.stream().map(c -> c.getSubredditName() + ": " + c.getBody()).collect(Collectors.toList()));

        List<SuccessEvent> successEvents = new ArrayList<>();
        List<FailEvent> failEvents = new ArrayList<>();

        for (Comment comment : canned) {
            try {
                respond(comment);
                successEvents.add(SuccessEvent.from(comment.getSubredditName(), comment.getId(), comment.getBody()));
            } catch (Exception e) {
                failEvents.add(FailEvent.from(comment.getSubredditName(), comment.getId(), comment.getBody(), e.getMessage()));
            }
        }

        if (!failEvents.isEmpty()) {
            log.warn("Got some failures: {}", failEvents);
        }

        logService.log(successEvents, failEvents);
    }

    public void respond(final Comment comment) {
        String response = responseService.applyTemplate(comment.getBody(), comment.getSubredditName());
        reddit.edit(comment, response);
        log.debug("Changed comment in {} with trigger {} to {}", comment.getSubredditName(), comment.getBody(), response);
    }
}
