package com.nibado.project.redditcan.service;

import com.nibado.project.redditcan.reddit.Reddit;
import com.nibado.project.redditcan.service.domain.FailEvent;
import com.nibado.project.redditcan.service.domain.SuccessEvent;
import net.dean.jraw.models.Submission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LogService {
    private final Reddit reddit;
    private final Set<String> failedIs = new HashSet<>();

    @Autowired
    public LogService(final Reddit reddit) {
        this.reddit = reddit;
    }

    public void log(final List<SuccessEvent> successEvents, final List<FailEvent> failEvents) {
        List<FailEvent> newFailEvents = failEvents
                .stream()
                .filter(e -> failedIs.contains(e.getCommentId()))
                .collect(Collectors.toList());


        if (newFailEvents.isEmpty()) {
            return;
        }

        newFailEvents.stream().map(SuccessEvent::getCommentId).forEach(failedIs::add);

        Submission logTopic = reddit.getTodaysLogTopic().orElseGet(reddit::createLogTopic);

        String dateTime = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        StringBuilder builder = new StringBuilder(dateTime);
        builder.append("\n\n");
        builder.append("Successful replacements: ").append(successEvents.size()).append("  \n");
        builder.append("Unsuccessful replacements: ").append(failEvents.size()).append("  \n");
        builder.append("\n");
        for (FailEvent event : failEvents) {
            builder.append("* ")
                    .append(event.getSubReddit())
                    .append(" ")
                    .append(event.getCommand())
                    .append(": ")
                    .append(event.getMessage())
                    .append("\n");
        }

        reddit.reply(logTopic, builder.toString());
    }
}
