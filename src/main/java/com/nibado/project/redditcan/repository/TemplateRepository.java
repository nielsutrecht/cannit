package com.nibado.project.redditcan.repository;

import com.nibado.project.redditcan.repository.domain.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class TemplateRepository {
    private static final RowMapper<Template> TEMPLATE_ROW_MAPPER = (rs, rowNum) ->
            new Template(
                    rs.getString("sub"),
                    rs.getString("trigger"),
                    split(rs.getString("params")),
                    rs.getString("response"),
                    rs.getDate("lastUpdated"));

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TemplateRepository(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Template> get(final String sub, final String trigger) {
        return jdbcTemplate
                .query("SELECT sub, trigger, params, response, lastUpdated FROM templates WHERE sub = ? AND trigger = ?", TEMPLATE_ROW_MAPPER, sub, trigger)
                .stream()
                .findAny();
    }

    public void create(final String sub, final String trigger, final String response) {
        create(sub, trigger, Collections.emptyList(), response);
    }

    public void create(final String sub, final String trigger, final List<String> params, final String response) {
        jdbcTemplate.update("INSERT INTO templates(sub, trigger, params, response, lastUpdated) VALUES(?,?,?,?, NOW())", sub, trigger, join(params), response);
    }

    private static List<String> split(final String params) {
        if (params == null) {
            return Collections.emptyList();
        } else {
            return Stream.of(params.split(",\\s*"))
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
        }
    }

    private static String join(final List<String> params) {
        if (params.isEmpty()) {
            return null;
        } else {
            return params.stream().collect(Collectors.joining(","));
        }
    }
}
