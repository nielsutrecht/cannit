package com.nibado.project.redditcan.service.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@AllArgsConstructor
public class SuccessEvent {
    private final ZonedDateTime time;
    private final String subReddit;
    private final String commentId;
    private final String command;

    public static SuccessEvent from(final String subReddit, final String commentId, final String command) {
        return new SuccessEvent(ZonedDateTime.now(), subReddit, commentId, command);
    }
}
