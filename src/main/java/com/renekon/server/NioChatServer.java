package com.renekon.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.logging.Logger;

public class NioChatServer extends Server {
    private static final Logger LOGGER = Logger.getLogger(NioChatServer.class.getName());

    public NioChatServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void start() throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(address);
        SelectionLoop selectionLoop = new SelectionLoop(serverChannel, newConnections, connectionEvents);
        Thread selectionThread = new Thread(selectionLoop, "SelectionLoop");
        selectionThread.start();

        LOGGER.info("Chat server started at " + address);
    }
}
