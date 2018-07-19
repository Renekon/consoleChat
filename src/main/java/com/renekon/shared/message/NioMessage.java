package com.renekon.shared.message;

public class NioMessage implements Message {

    private MessageType type;
    private String author;
    private String text;

    NioMessage(MessageType type, String author) {
        this.author = author;
        this.type = type;
    }

    NioMessage(MessageType type, String author, String text) {
        this(type, author);
        this.text = text;
    }

    @Override
    public MessageType getType() {
        return type;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    public byte[] getBytes() {
        String separator = Character.toString((char) FIELD_DELIMITER);
        String end = Character.toString((char) MESSAGE_END);
        String string = type.toString() + separator + author + separator + text + end;
        return string.getBytes(CHARSET);
    }
}
