package com.renekon.client;

import com.renekon.shared.connection.Connection;

import java.util.Random;

public class SpamBot extends Client{
    static private final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static private final int MAX_WAIT_BEFORE_MESSAGE_MS = 10000;
    static private final int MAX_MESSAGE_LENGTH = 100;

    private final Random rng;

    private String randomString(int length) {
        char[] letters = new char[length];
        for (int i = 0; i < length; ++i) {
            letters[i] = LETTERS.charAt(rng.nextInt(LETTERS.length()));
        }
        return new String(letters);
    }

    public SpamBot(Connection connection) {
        super(connection);
        rng = new Random();
    }

    @Override
    public String readInput() {
        try {
            Thread.sleep(rng.nextInt(MAX_WAIT_BEFORE_MESSAGE_MS));
        } catch (InterruptedException ignored) {
        }

        int action = rng.nextInt(10);
        switch (action) {
            case 0:
                return "\\help";
            case 1:
                return "\\list";
            case 2:
                return "\\name " + randomString(10);
            case 3:
                return "\\exit";
            default:
                return randomString(rng.nextInt(MAX_MESSAGE_LENGTH));
        }
    }
}
