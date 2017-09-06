package com.nibado.project.redditcan.service.domain;

import lombok.Value;

import java.time.ZonedDateTime;
import java.util.List;

@Value
public class TaskResults {
    private final ZonedDateTime finished;
    private final List<SuccessEvent> successEvents;
    private final List<FailEvent> failEvents;
}
