package com.nibado.project.redditcan.service.exceptions;

public class TemplateNotFoundException extends RuntimeException {
    public TemplateNotFoundException(final String message) {
        super(message);
    }
}
