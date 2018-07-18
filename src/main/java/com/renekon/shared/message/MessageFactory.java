package com.renekon.shared.message;


public class MessageFactory {

    private final static String SERVER_MESSAGE_AUTHOR = "SERVER";

    static class InvalidMessageException extends RuntimeException {
    }

    public static Message createNameAcceptedMessage(String name) {
        return new ChatMessage(MessageType.NAME_ACCEPTED, name, null);
    }

    public static Message createNameRequestMessage() {
        return new ChatMessage(MessageType.NAME_REQUEST, SERVER_MESSAGE_AUTHOR);
    }

    public static Message createServerTextMessage(String text) {
        return new ChatMessage(MessageType.SERVER_TEXT, SERVER_MESSAGE_AUTHOR, text);
    }

    public static Message createDisconnectMessage() {
        return new ChatMessage(MessageType.DISCONNECT, SERVER_MESSAGE_AUTHOR);
    }

    public static Message createNameSentMessage(String name) {
        return new ChatMessage(MessageType.NAME_SENT, name);
    }

    public static Message createUserTextMessage(String name, String message) {
        return new ChatMessage(MessageType.USER_TEXT, name, message);
    }

    public static Message createFromBytes(byte[] bytes) throws InvalidMessageException {
        if (bytes == null) {
            return null;
        }

        if (bytes.length == 0 || bytes[bytes.length - 1] != Message.MESSAGE_END) {
            throw new InvalidMessageException();
        }

        String string = new String(bytes, 0, bytes.length - 1, Message.CHARSET);

        String delimiter = Character.toString((char) Message.FIELD_DELIMITER);
        String[] items = string.split(delimiter, -1);

        if (items.length != 3) {
            throw new InvalidMessageException();
        }

        String name = items[0];
        String author = items[1];
        String text = "null".equals(items[2]) ? null : items[2];
        MessageType type;
        try {
            type = MessageType.valueOf(name);
            return new ChatMessage(type, author, text);
        } catch (IllegalArgumentException e) {
            throw new InvalidMessageException();
        }
    }
}
