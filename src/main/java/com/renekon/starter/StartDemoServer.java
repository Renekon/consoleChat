package com.renekon.starter;

import com.renekon.server.chat.Chat;
import com.renekon.server.NioChatServer;

import java.io.IOException;
import java.net.InetSocketAddress;

class StartDemoServer {
    private static final int PORT = 5000;

    public static void main(String[] args) throws IOException {
        NioChatServer server = new NioChatServer(new InetSocketAddress(PORT));
        Chat chat = new Chat(server, 10, 100);
        chat.start(1);
    }
}
