package com.nibado.project.redditcan.reddit;

import org.junit.Before;
import org.junit.Test;

public class RedditTest {
    private Reddit reddit;

    @Before
    public void setup() throws Exception {
        reddit = new Reddit();
    }
    @Test
    public void me() throws Exception {
        System.out.println(new Reddit().me());
    }

    @Test
    public void comments() {
        reddit.comments().forEach(System.out::println);
    }

    @Test
    public void edit() {
        reddit.comments().stream().filter(c -> c.getBody().startsWith("/can")).forEach(c -> {
            reddit.edit(c, c.getBody() + "*");
        });
    }
}
