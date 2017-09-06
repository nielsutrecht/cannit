package com.nibado.project.redditcan.service;

import com.nibado.project.redditcan.repository.TemplateRepository;
import com.nibado.project.redditcan.repository.domain.Template;
import com.nibado.project.redditcan.service.exceptions.TemplateNotFoundException;
import freemarker.cache.TemplateLoader;
import org.h2.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.Optional;

@Service
public class TemplateService implements TemplateLoader {
    private final TemplateRepository repository;

    @Autowired
    public TemplateService(final TemplateRepository repository) {
        this.repository = repository;
    }

    public Template findTemplate(final String sub, final String trigger) {
        Optional<Template> template = repository.get(sub, trigger);

        if (!template.isPresent()) {
            template = repository.get("general", trigger);
        }

        return template.orElseThrow(() -> new TemplateNotFoundException(String.format("Template for %s, %s not found", sub, trigger)));
    }

    @Override
    public Object findTemplateSource(String name) throws IOException {
        String[] subAndTrigger = name.split("/");

        Optional<Template> template = repository.get(subAndTrigger[0], subAndTrigger[1]);

        return template.orElse(null);
    }

    @Override
    public long getLastModified(Object templateSource) {
        Template template = (Template) templateSource;
        return template.getLastUpdated().getTime();
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        if(!(templateSource instanceof Template)) {
            throw new RuntimeException("Expected Template, got " + templateSource.getClass().getCanonicalName());
        }

        Template template = (Template) templateSource;

        return new StringReader(template.getResponse());
    }

    @Override
    public void closeTemplateSource(Object o) throws IOException {
    }

    @PostConstruct
    public void init() {
        repository.create("general", "faq", resource("general/faq"));
        repository.create("general", "format", resource("general/format"));
        repository.create("general", "google", Collections.singletonList("query"), resource("general/google"));

        repository.create("javahelp", "faq", resource("javahelp/faq"));
        repository.create("learnprogramming", "faq", resource("learnprogramming/faq"));
    }

    private static String resource(final String key) {
        InputStream ins = TemplateService.class.getResourceAsStream("/responses/" + key + ".txt");
        try {
            return IOUtils.readStringAndClose(IOUtils.getReader(ins), -1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
