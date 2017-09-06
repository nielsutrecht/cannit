package com.nibado.project.redditcan.repository.domain;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Template {
    private final String sub;
    private final String trigger;
    private final List<String> params;
    private final String response;
    private final Date lastUpdated;
}
