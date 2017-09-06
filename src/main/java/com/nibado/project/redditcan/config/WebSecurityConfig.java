package com.nibado.project.redditcan.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    public WebSecurityConfig() {
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/home", "/response").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .permitAll();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        String password = randomPassword();
        auth
                .inMemoryAuthentication()
                .withUser("admin").password(password).roles("USER");

        System.out.println("***************************************");
        System.out.println("* User: admin                         *");
        System.out.printf ("* Pass: %s                    *\n", password);
        System.out.println("***************************************");
    }

    private static String randomPassword() {
        List<Character> chars = new ArrayList<>(26 * 2 + 10);
        Random random = new Random();

        for (int i = 0; i < 26; i++) {
            char c = (char) (i + 'a');

            chars.add(c);
            chars.add(Character.toUpperCase(c));
            if (c <= 9) {
                chars.add(Character.forDigit(i, 10));
            }
        }

        StringBuilder builder = new StringBuilder(10);

        for (int i = 0; i < 10; i++) {
            builder.append(chars.get(random.nextInt(chars.size())));
        }

        return builder.toString();
    }
}
