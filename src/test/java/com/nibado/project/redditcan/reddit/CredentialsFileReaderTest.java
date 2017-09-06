package com.nibado.project.redditcan.reddit;

import org.junit.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class CredentialsFileReaderTest {
    @Test
    public void create() throws Exception {
        Properties p =  CredentialsFileReader.create("user", "password", "clientId", "clientSecret");
        RedditCredentials credentials = CredentialsFileReader.get(p);

        assertThat(credentials.getUser()).isEqualTo("user");
        assertThat(credentials.getPassword()).isEqualTo("password");
        assertThat(credentials.getClientId()).isEqualTo("clientId");
        assertThat(credentials.getClientSecret()).isEqualTo("clientSecret");
    }

}
