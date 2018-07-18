package com.renekon.shared.message;

import java.nio.charset.Charset;

public interface Message {
    byte FIELD_DELIMITER = 0x1F;
    byte MESSAGE_END = 0x1E;
    Charset CHARSET = Charset.forName("UTF-8");

    MessageType getType();

    byte[] getBytes();

    String getText();

    String getPrintText();

    String getAuthor();
}
