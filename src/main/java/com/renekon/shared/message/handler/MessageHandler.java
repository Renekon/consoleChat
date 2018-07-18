package com.renekon.shared.message.handler;

import com.renekon.shared.message.Message;
import com.renekon.shared.connection.Connection;

public interface MessageHandler {
    void execute(Message message, Connection connection);
}
