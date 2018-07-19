package com.renekon.shared.message;


public abstract class MessageFactory {

    private final static String SERVER_MESSAGE_AUTHOR = "SERVER";

    static class InvalidMessageException extends RuntimeException {
        InvalidMessageException(String message) {
            super(message);
        }
    }

    public Message createNameAcceptedMessage(String name) {
        return new NioMessage(MessageType.NAME_ACCEPTED, name, null);
    }

    public Message createNameRequestMessage() {
        return new NioMessage(MessageType.NAME_REQUEST, SERVER_MESSAGE_AUTHOR);
    }

    public Message createServerTextMessage(String text) {
        return new NioMessage(MessageType.SERVER_TEXT, SERVER_MESSAGE_AUTHOR, text);
    }

    public Message createDisconnectMessage() {
        return new NioMessage(MessageType.DISCONNECT, SERVER_MESSAGE_AUTHOR);
    }

    public Message createNameSentMessage(String name) {
        return new NioMessage(MessageType.NAME_SENT, name);
    }

    public Message createUserTextMessage(String name, String message) {
        return new NioMessage(MessageType.USER_TEXT, name, message);
    }
}
