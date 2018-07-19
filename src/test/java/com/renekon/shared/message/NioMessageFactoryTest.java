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
//            Assertions.assertEquals(message, messageFromBytes);
        }
    }

    @Test
    void testInvalidMessageException() {
        NioMessageFactory messageFactory = new NioMessageFactory();
        Message message = messageFactory.createUserTextMessage("Lena", "bye");
        byte[] bytes = ((NioMessage) message).getBytes();
        bytes[2] = (byte) 'X';

        Assertions.assertThrows(MessageFactory.InvalidMessageException.class,
                () -> messageFactory.createFromBytes(bytes));
    }
}
