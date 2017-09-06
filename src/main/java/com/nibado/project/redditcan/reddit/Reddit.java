package com.nibado.project.redditcan.reddit;

import net.dean.jraw.ApiException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.fluent.FluentRedditClient;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.managers.AccountManager;
import net.dean.jraw.models.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Reddit {
    private static final UserAgent USER_AGENT = UserAgent.of("server", "com.nibado.project.redditcan", "v0.1", "nutrecht");
    private static final Set<String> WHITE_LIST = new HashSet<>(Arrays.asList("test"));

    private RedditClient client;
    private FluentRedditClient fluent;
    private String userName;

    public void login(final RedditCredentials credentials) throws OAuthException {
        client = new RedditClient(USER_AGENT);
        OAuthData authData = client.getOAuthHelper().easyAuth(Credentials.script(
                credentials.getUser(),
                credentials.getPassword(),
                credentials.getClientId(),
                credentials.getClientSecret()));
        client.authenticate(authData);

        fluent = new FluentRedditClient(client);

        this.userName = me().getFullName();
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

    public List<Submission> getCannitTopics() {
        Listing<Submission> submissions = fluent.subreddit("cannit").newest().fetch();

        return submissions.stream()
                .filter(s -> s.getAuthor().equals(userName))
                .collect(Collectors.toList());
    }

    public Optional<Submission> getTodaysLogTopic() {
        return getCannitTopics()
                .stream()
                .filter(s -> s.getTitle().startsWith("log"))
                .filter(s -> s.getTitle().equals("log " + LocalDate.now().format(DateTimeFormatter.ISO_DATE)))
                .findAny();
    }

    public void reply(final Submission submission, final String text) {
        try {
            new AccountManager(client).reply(submission, text);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    public Submission createLogTopic() {
        String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        try {
            return fluent.subreddit("cannit").submit("Log topic for " + date, "log " + date);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }
}
