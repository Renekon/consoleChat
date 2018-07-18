package com.renekon.server.connection;

import com.renekon.shared.connection.*;
import com.renekon.shared.connection.event.CloseConnectionEvent;
import com.renekon.shared.connection.event.ConnectionEvent;
import com.renekon.shared.connection.event.DataReceivedEvent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NioConnectionManager implements ConnectionManager {
    private static final Logger LOGGER = Logger.getLogger(NioConnectionManager.class.getName());

    private final Selector selector;

    private ArrayBlockingQueue<Connection> newConnections;
    private ArrayBlockingQueue<ConnectionEvent> connectionEvents;
    private final ModeChangeRequestQueue modeChangeRequestQueue;

    public NioConnectionManager(InetSocketAddress address) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(address);
        serverChannel.configureBlocking(false);
        this.selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        this.modeChangeRequestQueue = new ModeChangeRequestQueue(this.selector);
    }

    @Override
    public void bindConnectionQueues(ArrayBlockingQueue<Connection> newConnectionQueue, ArrayBlockingQueue<ConnectionEvent> connectionEventsQueue) {
        this.newConnections = newConnectionQueue;
        this.connectionEvents = connectionEventsQueue;
    }

    @Override
    public void run() {
        while (true) {
            modeChangeRequestQueue.process();
            try {
                selector.select();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error selecting IO channel", e);
                continue;
            }

            Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
            while (selectedKeys.hasNext()) {
                SelectionKey key = selectedKeys.next();
                selectedKeys.remove();
                if (!key.isValid()) {
                    continue;
                }
                if (key.isAcceptable()) {
                    accept(key);
                } else if (key.isReadable()) {
                    read(key);
                } else if (key.isWritable()) {
                    write(key);
                }
            }
        }
    }

    private void accept(SelectionKey key) {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        SocketChannel socketChannel;
        try {
            socketChannel = serverSocketChannel.accept();
            logWarning("Connection from " + socketChannel.getRemoteAddress().toString());
        } catch (IOException e) {
            logWarning("Error accepting connection", e);
            return;
        }

        NioSocketConnection connection;
        try {
            connection = new NioSocketConnection(selector, socketChannel, modeChangeRequestQueue);
            newConnections.put(connection);
        } catch (IOException e) {
            logWarning("Error creating connection from SocketChannel", e);
        } catch (InterruptedException e) {
            logWarning("Interrupted while putting new Connection");
        }
    }

    private void read(SelectionKey key) {
        NioSocketConnection connection = (NioSocketConnection) key.attachment();

        try {
            connection.readFromChannel();
            connectionEvents.put(new DataReceivedEvent(connection));
        } catch (IOException e) {
            key.cancel();
            close(connection);
        } catch (InterruptedException e) {
            logWarning( "Interrupted while putting DATA ConnectionEvent");
        }
    }

    private void write(SelectionKey key) {
        NioSocketConnection connection = (NioSocketConnection) key.attachment();
        try {
            connection.writeToChannel();
        } catch (IOException e) {
            close(connection);
            return;
        }

        if (connection.shouldClose() && connection.nothingToWrite()) {
            close(connection);
        }
    }

    private void close(NioSocketConnection connection) {
        try {
            connection.channel.close();
            connectionEvents.put(new CloseConnectionEvent(connection));
        } catch (IOException e) {
            logWarning("Error closing connection", e);
        } catch (InterruptedException e) {
            logWarning("Interrupted while putting CLOSE ConnectionEvent");
        }
    }

    private void logWarning(String msg){
        LOGGER.log(Level.WARNING,msg);
    }

    private void logWarning(String msg, Throwable e){
        LOGGER.log(Level.WARNING,msg, e);
    }
}
