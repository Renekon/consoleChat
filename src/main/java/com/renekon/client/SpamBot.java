package com.renekon.client;

import com.renekon.shared.connection.Connection;

import java.util.concurrent.ThreadLocalRandom;

public class SpamBot extends Client {
    static private final int MIN_WAIT_BEFORE_MESSAGE_MS = 500;
    static private final int MAX_WAIT_BEFORE_MESSAGE_MS = 1000;
    static private final int MAX_MESSAGE_LENGTH = 20;
    static private String[] WORDS = {" youth", " parachute", " cup", " incapable", " border", " chop", " palm", " begin",
            " provincial", " undertake", " long", " murder", " career", " see", " crash", " worry", " glass", " man", " whisper",
            " superintendent", " lead", " straight", " bin", " state", " navy", " stand", " wine", " fun", " ticket", " intermediate",
            " similar", " architecture", " design", " variety", " mention", " indoor", " choose", " smell", " support", " just", ",", "."};


    private String randomString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            sb.append(WORDS[ThreadLocalRandom.current().nextInt(WORDS.length)]);
        }
        return sb.toString().trim();
    }

    public SpamBot(Connection connection) {
        super(connection);
    }

    @Override
    void stop() {
        running = false;
        executorService.shutdown();
    }

    @Override
    boolean hasInput() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(MIN_WAIT_BEFORE_MESSAGE_MS, MAX_WAIT_BEFORE_MESSAGE_MS));
            return true;
        } catch (InterruptedException ignored) {
            return false;
        }
    }

    @Override
    String readInput() {
        int action = ThreadLocalRandom.current().nextInt(20);
        switch (action) {
            case 0:
                return "/help";
            case 1:
                return "/userslist";
            case 2:
                return "/changename " + randomString(2);
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
