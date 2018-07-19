package com.renekon.shared.message;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NioMessageFactoryTest {
    @Test
    void testFromToBytesConversion() {
        NioMessageFactory messageFactory = new NioMessageFactory();
        Message messages[] = {
                messageFactory.createUserTextMessage("Mike", "hi"),
                messageFactory.createNameAcceptedMessage("Nick"),
                messageFactory.createNameRequestMessage(),
                messageFactory.createNameSentMessage("Andrew"),
                messageFactory.createServerTextMessage("hello"),
                messageFactory.createDisconnectMessage()
        };

        for (Message message : messages) {
            Message messageFromBytes = messageFactory.createFromBytes(((NioMessage) message).getBytes());
            Assertions.assertEquals(message.getType(), messageFromBytes.getType());
            Assertions.assertEquals(message.getAuthor(), messageFromBytes.getAuthor());
            Assertions.assertEquals(message.getText(), messageFromBytes.getText());
        }
        Assertions.assertNull(messageFactory.createFromBytes(null));
    }

    @Test
    void testInvalidMessageException() {
        NioMessageFactory messageFactory = new NioMessageFactory();

        Assertions.assertNull(messageFactory.createFromBytes(null));
        Assertions.assertThrows(MessageFactory.InvalidMessageException.class,
                () -> messageFactory.createFromBytes(new byte[0]));

        Message message = messageFactory.createUserTextMessage("Lena", "bye");
        byte[] messageWithInvalidType = ((NioMessage) message).getBytes();
        messageWithInvalidType[2] = (byte) 'X';
        Assertions.assertThrows(MessageFactory.InvalidMessageException.class,
                () -> messageFactory.createFromBytes(messageWithInvalidType));

        byte[] messageWithNotEnoughFields = {2, Message.FIELD_DELIMITER, 5, Message.MESSAGE_END};
        Assertions.assertThrows(MessageFactory.InvalidMessageException.class,
                () -> messageFactory.createFromBytes(messageWithNotEnoughFields));

    }
}
