package com.renekon.client;

import com.renekon.shared.message.MessageType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ClientTest {
    @Test
    void registerMessageHandlers(){
        Client client = new Client(null);
        Assertions.assertNotNull(client.messageHandlers.get(MessageType.NAME_REQUEST));
        Assertions.assertNotNull(client.messageHandlers.get(MessageType.NAME_ACCEPTED));
        Assertions.assertNotNull(client.messageHandlers.get(MessageType.NAME_SENT));
        Assertions.assertNotNull(client.messageHandlers.get(MessageType.DISCONNECT));
    }

    @Test
    void startAndStop(){
        Client client = new Client(null);
        client.startInputScanner();
        Assertions.assertFalse(client.executorService.isShutdown());
        client.executorService.shutdown();
        Assertions.assertTrue(client.executorService.isShutdown());
    }
}
