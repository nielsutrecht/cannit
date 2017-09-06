package com.nibado.project.redditcan.service.domain;

import lombok.Getter;
import lombok.ToString;

import java.time.ZonedDateTime;

@Getter
@ToString
public class FailEvent extends SuccessEvent {
    private final String message;

    public FailEvent(final ZonedDateTime time, final String subReddit, final String commentId, final String command, final String message) {
        super(time, subReddit, commentId, command);
        this.message = message;
    }

    public static FailEvent from(final String subReddit, final String commentId, final String command, final String message) {
        return new FailEvent(ZonedDateTime.now(), subReddit, commentId, command, message);
    }

}
