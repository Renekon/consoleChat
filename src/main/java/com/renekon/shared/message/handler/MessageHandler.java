package com.renekon.shared.message.handler;

import com.renekon.shared.connection.Connection;
import com.renekon.shared.message.Message;

public interface MessageHandler {
    void execute(Message message, Connection connection);
}
