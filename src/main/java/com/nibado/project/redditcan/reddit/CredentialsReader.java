package com.nibado.project.redditcan.reddit;

import java.io.IOException;

public interface CredentialsReader {
    RedditCredentials get() throws IOException;
}
