package com.renekon.shared.message.handler;

import com.renekon.shared.message.MessageType;

import java.util.HashMap;

public class MessageHandlerFactory {
    private final HashMap<MessageType, MessageHandler> handlers = new HashMap<>();

    public void register(MessageType type, MessageHandler handler) {
        handlers.put(type, handler);
    }

    public MessageHandler get(MessageType type) {
        return handlers.get(type);
    }
}
