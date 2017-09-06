package com.nibado.project.redditcan.repository;

import com.nibado.project.redditcan.repository.domain.Template;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class TemplateRepositoryTest extends RepositoryTest<Template> {
    private TemplateRepository repository;

    public TemplateRepositoryTest() {
        super("templates");
    }

    @Before
    public void setup() {
        super.setup();

        repository = new TemplateRepository(template);
    }

    @Test
    public void get() throws Exception {
        repository.create("test", "test", Arrays.asList("a", "b", "c"), "foo bar baz");
        System.out.println(repository.get("test", "test").get());
    }

}
