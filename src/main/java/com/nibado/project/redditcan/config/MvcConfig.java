package com.nibado.project.redditcan.config;

import com.nibado.project.redditcan.reddit.CredentialsFileReader;
import com.nibado.project.redditcan.reddit.Reddit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Bean
    public Reddit reddit() throws Exception {
        Reddit reddit = new Reddit();
        reddit.login(new CredentialsFileReader().get());
        return reddit;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/home").setViewName("home");
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/hello").setViewName("hello");
        registry.addViewController("/login").setViewName("login");
    }
}
