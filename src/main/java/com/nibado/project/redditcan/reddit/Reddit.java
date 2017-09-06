package com.nibado.project.redditcan.reddit;

import net.dean.jraw.ApiException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.fluent.FluentRedditClient;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.managers.AccountManager;
import net.dean.jraw.models.Comment;
import net.dean.jraw.models.Contribution;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.LoggedInAccount;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Reddit {
    private static final UserAgent USER_AGENT = UserAgent.of("server", "com.nibado.project.redditcan", "v0.1", "nutrecht");
    private static final Set<String> WHITE_LIST = new HashSet<>(Arrays.asList("test"));

    private RedditClient client;
    private FluentRedditClient fluent;

    public void login(final String username, final String password) throws OAuthException {
        Properties properties = new Properties();
        try {
            properties.load(Reddit.class.getResourceAsStream("/secret.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Credentials credentials = Credentials.script(
                username,
                password,
                properties.getProperty("clientId"),
                properties.getProperty("clientSecret"));

        client = new RedditClient(USER_AGENT);
        OAuthData authData = client.getOAuthHelper().easyAuth(credentials);
        client.authenticate(authData);

        fluent = new FluentRedditClient(client);
    }

    public LoggedInAccount me() {
        return client.me();
    }

    public List<Comment> comments() {
        List<Comment> comments = getComments(fluent.me().comments().next());

        return comments.stream()
                .filter(c -> WHITE_LIST.contains(c.getSubredditName()))
                .collect(Collectors.toList());
    }

    public void edit(final Comment comment, final String text) {
        try {
            new AccountManager(client).updateContribution(comment, text);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Comment> getComments(final Listing<Contribution> contributions) {
        return contributions
                .stream()
                .filter(c -> c instanceof Comment)
                .map(c -> (Comment) c)
                .collect(Collectors.toList());
    }
}
