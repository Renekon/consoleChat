package com.renekon.client;

import com.renekon.shared.connection.Connection;

import java.util.concurrent.ThreadLocalRandom;

public class SpamBot extends Client {
    static private final String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static private final int MAX_WAIT_BEFORE_MESSAGE_MS = 250;
    static private final int MAX_MESSAGE_LENGTH = 100;

    private final ThreadLocalRandom rng;

    private String randomString(int length) {
        char[] letters = new char[length];
        for (int i = 0; i < length; ++i) {
            letters[i] = LETTERS.charAt(rng.nextInt(LETTERS.length()));
        }
        return new String(letters);
    }

    public SpamBot(Connection connection) {
        super(connection);
        rng = ThreadLocalRandom.current();
    }

    @Override
    String readInput() {
        try {
            Thread.sleep(rng.nextInt(MAX_WAIT_BEFORE_MESSAGE_MS));
        } catch (InterruptedException ignored) {
        }

        int action = rng.nextInt(10);
        switch (action) {
            case 0:
                return "\\help";
            case 1:
                return "\\userslist";
            case 2:
                return "\\changename " + randomString(10);
            case 3:
                return "\\quit";
            default:
                return randomString(rng.nextInt(MAX_MESSAGE_LENGTH));
        }
    }

    @Override
    void displayText(String text) {
    }
}
