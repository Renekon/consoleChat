package com.renekon.client;

import com.renekon.shared.connection.Connection;

import java.util.concurrent.ThreadLocalRandom;

public class SpamBot extends Client {
    static private final String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static private final int MIN_WAIT_BEFORE_MESSAGE_MS = 500;
    static private final int MAX_WAIT_BEFORE_MESSAGE_MS = 1000;
    static private final int MAX_MESSAGE_LENGTH = 100;


    private String randomString(int length) {
        char[] letters = new char[length];
        for (int i = 0; i < length; ++i) {
            letters[i] = LETTERS.charAt(ThreadLocalRandom.current().nextInt(LETTERS.length()));
        }
        return new String(letters);
    }

    public SpamBot(Connection connection) {
        super(connection);
    }

    @Override
    void stop() {
        running = false;
        System.out.println("Bot stopped");
        executorService.shutdown();
    }

    @Override
    boolean hasInput(){
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(MIN_WAIT_BEFORE_MESSAGE_MS, MAX_WAIT_BEFORE_MESSAGE_MS));
            return true;
        } catch (InterruptedException ignored) {
            return false;
        }
    }

    @Override
    String readInput() {
        int action = ThreadLocalRandom.current().nextInt(50);
        switch (action) {
            case 0:
                return "/help";
            case 1:
                return "/userslist";
            case 2:
                return "/changename " + randomString(10);
            case 3:
                return "/quit";
            default:
                return randomString(ThreadLocalRandom.current().nextInt(MAX_MESSAGE_LENGTH));
        }
    }

    @Override
    void displayText(String text) {
    }
}
