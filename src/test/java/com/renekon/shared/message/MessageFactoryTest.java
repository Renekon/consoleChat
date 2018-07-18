package com.renekon.shared.message;

import com.renekon.shared.message.Message;
import com.renekon.shared.message.MessageFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MessageFactoryTest {
    @Test
    void testFromToBytesConversion() {
        Message messages[] = {
                MessageFactory.createUserTextMessage("Mike", "hi"),
                MessageFactory.createNameAcceptedMessage("Nick"),
                MessageFactory.createNameRequestMessage(),
                MessageFactory.createNameSentMessage("Andrew"),
                MessageFactory.createServerTextMessage("hello"),
                MessageFactory.createDisconnectMessage()
        };

        for (Message message : messages) {
            Message messageFromBytes = MessageFactory.createFromBytes(message.getBytes());
            Assertions.assertEquals(message, messageFromBytes);
        }
    }

    @Test
    void testInvalidMessageException() {
        Message message = MessageFactory.createUserTextMessage("Lena", "bye");
        byte[] bytes = message.getBytes();
        bytes[2] = (byte) 'X';

        Assertions.assertThrows(MessageFactory.InvalidMessageException.class,
                () -> MessageFactory.createFromBytes(bytes));
    }
}
