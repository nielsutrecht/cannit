package com.nibado.project.redditcan.reddit;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Value
@Slf4j
/**
 * Can read Reddit credentials from an obfuscated secret.properties file.
 */
public class RedditCredentials {
    private final String user;
    private final String password;
    private final String clientId;
    private final String clientSecret;
}
