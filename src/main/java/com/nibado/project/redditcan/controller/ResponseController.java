package com.nibado.project.redditcan.controller;

import com.nibado.project.redditcan.service.ResponseService;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/response")
@Slf4j
public class ResponseController {
    private final ResponseService service;

    public ResponseController(final ResponseService service) {
        this.service = service;
    }

    @GetMapping
    public Response response(
            @RequestParam("sub")final String sub,
            @RequestParam("trigger") final String trigger,
            @RequestParam(value = "params", required = false) final String params) {
        return new Response(service.applyTemplate("/can " + trigger + " " + (params == null ? "" : params), sub));
    }

    @Value
    public static class Response {
        private final String response;
    }
}
