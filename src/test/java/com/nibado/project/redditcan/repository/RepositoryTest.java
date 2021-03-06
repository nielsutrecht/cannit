package com.nibado.project.redditcan.repository;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class RepositoryTest<T> {
    private final String table;

    protected EmbeddedDatabase database;
    protected JdbcTemplate template;


    public RepositoryTest(final String table) {
        this.table = table;
    }

    @Rule
    public ExpectedException expected = ExpectedException.none();

    public void setup() {
        database = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:sql/schema.sql")
                .build();

        template = new JdbcTemplate(database);

        JdbcTestUtils.deleteFromTables(template, table);

        assertEmpty();
    }

    public void teardown() {
        JdbcTestUtils.deleteFromTables(template, table);
    }

    protected void assertRows(int amount) {
        assertThat(JdbcTestUtils.countRowsInTable(template, table)).isEqualTo(amount);
    }

    protected List<Map<String, Object>> allRows() {
        return template.queryForList("SELECT * FROM " + table);
    }

    protected Map<String, Object> singleRow() {
        return allRows().remove(0);
    }

    protected void assertEmpty() {
        assertRows(0);
    }
}
