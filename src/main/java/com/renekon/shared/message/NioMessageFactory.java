package com.renekon.shared.message;

public class NioMessageFactory extends MessageFactory {
    private final static String delimiter = Character.toString((char) Message.FIELD_DELIMITER);

    public static Message createFromBytes(byte[] bytes) throws InvalidMessageException {
        if (!bytesAreValid(bytes))
            return null;
        String[] fields = extractMessageFields(bytes);
        return createFromFields(fields);
    }

    private static boolean bytesAreValid(byte[] bytes) {
        if (bytes == null)
            return false;
        if (bytes.length == 0 || bytes[bytes.length - 1] != Message.MESSAGE_END)
            throw new InvalidMessageException("Message is empty of does not have end");
        return true;
    }

    private static String[] extractMessageFields(byte[] bytes) {
        String messageString = new String(bytes, 0, bytes.length - 1, Message.CHARSET);
        String[] items = messageString.split(delimiter, -1);

        if (items.length != 3) {
            throw new InvalidMessageException("Invalid message fields count");
        }
        return items;
    }

    private static Message createFromFields(String[] fields) {
        String name = fields[0];
        String author = fields[1];
        String text = "null".equals(fields[2]) ? null : fields[2];
        try {
            MessageType type = MessageType.valueOf(name);
            return new NioMessage(type, author, text);
        } catch (IllegalArgumentException e) {
            throw new InvalidMessageException("Invalid message type");
        }
    }
}
