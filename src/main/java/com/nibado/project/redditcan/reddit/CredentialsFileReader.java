package com.nibado.project.redditcan.reddit;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class CredentialsFileReader implements CredentialsReader {
    public RedditCredentials get() throws IOException {
        Properties properties = new Properties();

        File secretFile = new File(System.getProperty("user.home") + "/.credentials/reddit/secret.properties");
        InputStream ins;
        if (secretFile.exists()) {
            ins = new FileInputStream(secretFile);
        } else {
            log.info("{} does not exist.", secretFile.getAbsolutePath());

            ins = Reddit.class.getResourceAsStream("/secret.properties");
        }

        if (ins == null) {
            log.error("Could not load Reddit credentials from file or classpath");
            throw new IOException("Could not load Reddit credentials from file or classpath");
        }

        properties.load(ins);

        return get(properties);
    }

    public static RedditCredentials get(final Properties properties) {
        Aes aes = new Aes(properties.getProperty("clientId"));

        return new RedditCredentials(
                aes.decrypt(properties.getProperty("user")),
                aes.decrypt(properties.getProperty("password")),
                dec64(properties.getProperty("clientId")),
                aes.decrypt(properties.getProperty("clientSecret")));
    }

    public static void main(String... argv) throws Exception {
        if (argv.length < 4) {
            System.err.println("Usage: java RedditCredentials <user> <pass> <clientId> <clientSecret>");
            return;
        }

        create(argv[0], argv[1], argv[2], argv[3]).store(System.out, null);
    }

    public static Properties create(final String user, final String password, final String clientId, final String clientSecret) {
        String clientId64 = enc64(clientId);

        Aes aes = new Aes(clientId64);

        Properties properties = new Properties();

        properties.put("user", aes.encrypt(user));
        properties.put("password", aes.encrypt(password));
        properties.put("clientId", clientId64);
        properties.put("clientSecret", aes.encrypt(clientSecret));

        return properties;
    }

    private static String enc64(final String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(UTF_8));
    }

    private static String dec64(final String enc) {
        return new String(Base64.getDecoder().decode(enc), UTF_8);
    }

    public static class Aes {
        private final byte[] key;

        public Aes(final String key) {
            this.key = makeKey(key);
        }

        private static byte[] makeKey(final String key) {
            MessageDigest sha = null;
            try {
                sha = MessageDigest.getInstance("SHA-1");
                return Arrays.copyOf(sha.digest(key.getBytes(UTF_8)), 16);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        public String encrypt(final String plainText) {
            SecretKeySpec k = new SecretKeySpec(key, "AES");
            try {
                Cipher c = Cipher.getInstance("AES");
                c.init(Cipher.ENCRYPT_MODE, k);
                byte[] encryptedData = c.doFinal(plainText.getBytes(UTF_8));

                return Base64.getEncoder().encodeToString(encryptedData);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public String decrypt(final String encryptedText) {
            SecretKeySpec k = new SecretKeySpec(key, "AES");
            try {
                Cipher c = Cipher.getInstance("AES");
                c.init(Cipher.DECRYPT_MODE, k);
                byte[] data = c.doFinal(Base64.getDecoder().decode(encryptedText));
                return new String(data, UTF_8);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
