package com.renekon.shared.message;

public class ChatMessage implements Message {

    private MessageType type;
    private String author;
    private String text;

    ChatMessage(MessageType type, String author) {
        this.author = author;
        this.type = type;
    }

    ChatMessage(MessageType type, String author, String text) {
        this(type, author);
        this.text = text;
    }

    @Override
    public MessageType getType() {
        return type;
    }

    @Override
    public byte[] getBytes() {
        String separator = Character.toString((char) FIELD_DELIMITER);
        String end = Character.toString((char) MESSAGE_END);
        String string = type.toString() + separator + author + separator + text + end;
        return string.getBytes(CHARSET);
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getTextWithAuthor() {
        return author + ": " + text;
    }

    @Override
    public String getAuthor() {
        return author;
    }
}
