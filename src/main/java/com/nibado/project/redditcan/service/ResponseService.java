package com.nibado.project.redditcan.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ResponseService {
    private final Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
    private static final Pattern COMMAND_PATTERN = Pattern.compile("/can ([a-z]+)(.+)");

    private final TemplateService service;

    @Autowired
    public ResponseService(final TemplateService service) {
        this.service = service;
        cfg.setTemplateLoader(service);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
    }

    public String applyTemplate(final String command, final String sub) {
        Matcher m = COMMAND_PATTERN.matcher(command);
        if (!m.matches()) {
            throw new IllegalArgumentException("Invalid command: " + command);
        }

        String trigger = m.group(1);

        com.nibado.project.redditcan.repository.domain.Template template = service.findTemplate(sub, trigger);

        Map<String, Object> model = parseModel(template, m.group(2));

        try {
            Template temp = cfg.getTemplate(template.getSub() + "/" + template.getTrigger());
            Writer out = new OutputStreamWriter(System.out);
            temp.process(model, out);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "";
    }

    public Map<String, Object> parseModel(final com.nibado.project.redditcan.repository.domain.Template template, final String params) {
        Map<Integer, String> paramMap = new HashMap<>();
        for (int i = 0; i < template.getParams().size(); i++) {
            paramMap.put(i, template.getParams().get(i));
        }

        Map<String, Object> model = new HashMap<>();
        model.put("subreddit", template.getSub());
        model.put("trigger", template.getTrigger());

        if (params == null || params.trim().isEmpty()) {
            return model;
        }

        List<String> paramList = Stream.of(params.trim().split("/")).map(String::trim).collect(Collectors.toList());


        for (int i = 0; i < paramList.size(); i++) {
            if (!paramMap.containsKey(i)) {
                log.warn("Parameter with index {} and value {} nto found in list {}", i, paramList.get(i), template.getParams());
            } else if (!paramList.get(i).isEmpty()) {
                model.put(paramMap.get(i), paramList.get(i));
            }
        }

        return model;
    }

    public String run() throws Exception {
        Map<String, Object> root = new HashMap<>();
        root.put("subreddit", "test");
        root.put("query", "java programming");

        Template temp = cfg.getTemplate("/general/google.txt");
        Writer out = new OutputStreamWriter(System.out);
        temp.process(root, out);

        return null;
    }
}
